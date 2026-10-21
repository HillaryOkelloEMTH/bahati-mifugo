package com.emtech.dairyapp.Auth.User;

import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.AuthRequest;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.AuthResponse;
import com.emtech.dairyapp.Auth.Data.User.UserData;
import com.emtech.dairyapp.Auth.RefreshToken.RefreshToken;
import com.emtech.dairyapp.Auth.RefreshToken.RefreshTokenRepo;
import com.emtech.dairyapp.Auth.RefreshToken.RefreshTokenService;
import com.emtech.dairyapp.Auth.Role.Role;
import com.emtech.dairyapp.Auth.Role.RoleRepository;
import com.emtech.dairyapp.Auth.UserRole.UserRole;
import com.emtech.dairyapp.Auth.UserRole.UserRoleRepository;
import com.emtech.dairyapp.Auth.Utilities.JWTUtil;
import com.emtech.dairyapp.Auth.Utilities.PasswordUtil;
import com.emtech.dairyapp.Configurations.FarmerManagement.Farmer;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepository userRepository;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final FarmerRepo farmerRepo;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public EntityResponse<?> authenticateUser(AuthRequest authRequest){
        EntityResponse<AuthResponse> response = new EntityResponse<>();

        try {
            String rawIdentifier = authRequest.getUsername() != null ? authRequest.getUsername().trim() : "";
            String rawPassword = authRequest.getPassword() != null ? authRequest.getPassword().trim() : "";

            if (rawIdentifier.isEmpty()) {
                response.setMessage("Username or identification number is required");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }

            if (rawPassword.isEmpty()) {
                response.setMessage("Password is required");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }

            // 1. Lookup existing user in the users table
            Optional<User> userOpt = findExistingUserByIdentifier(rawIdentifier);

            // 2. If not found in users table, lookup in farmer registry
            if (userOpt.isEmpty()) {
                Optional<Farmer> farmerOpt = findFarmerByIdentifier(rawIdentifier);
                if (farmerOpt.isPresent()) {
                    Farmer matchedFarmer = farmerOpt.get();

                    // Check if a User row was already created for this farmer under another identifier
                    if (matchedFarmer.getIdNumber() != null && !matchedFarmer.getIdNumber().isBlank()) {
                        userOpt = userRepository.findByUsername(matchedFarmer.getIdNumber().trim());
                    }
                    if (userOpt.isEmpty() && matchedFarmer.getFarmerNo() != null) {
                        userOpt = userRepository.findByUsername("farmer_" + matchedFarmer.getFarmerNo());
                    }
                    if (userOpt.isEmpty() && matchedFarmer.getMobileNo() != null && !matchedFarmer.getMobileNo().isBlank()) {
                        userOpt = findUserByMobileNumber(matchedFarmer.getMobileNo().trim());
                    }

                    // If still no User row exists for this registered farmer:
                    if (userOpt.isEmpty()) {
                        if (isInitialFarmerPasswordMatch(matchedFarmer, rawPassword)) {
                            User provisioned = provisionFarmerUser(matchedFarmer, rawPassword);
                            userOpt = Optional.of(provisioned);
                        } else {
                            log.warn("Farmer found for '{}' but onboarding credentials do not match", rawIdentifier);
                            response.setMessage("Sign in failed: 'username or password is incorrect'");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            return response;
                        }
                    }
                }
            }

            // 3. Neither a User account nor a Farmer was found
            if (userOpt.isEmpty()) {
                log.warn("No active user account or registered member found for identifier '{}'", rawIdentifier);
                response.setMessage("User is not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

            User user = userOpt.get();

            // 4. Check user status
            String status = user.getStatus();
            if (status != null && !status.equalsIgnoreCase("Active")) {
                log.warn("Account for user '{}' is not active (status: {})", user.getUsername(), status);
                if (status.equalsIgnoreCase("Deactivated") || status.equalsIgnoreCase("Suspended") || status.equalsIgnoreCase("Blocked")) {
                    response.setMessage("Account is deactivated or suspended. Please contact administrator.");
                } else {
                    response.setMessage("Account is inactive or pending approval. Please contact administrator.");
                }
                response.setStatusCode(HttpStatus.FORBIDDEN.value());
                return response;
            }

            // 5. Verify password credentials
            log.info("Checking credentials for user '{}'", user.getUsername());
            if (!passwordUtil.matches(rawPassword, user.getPassword())) {
                log.warn("Password mismatch for user '{}'", user.getUsername());
                response.setMessage("Sign in failed: 'username or password is incorrect'");
                response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                return response;
            }

            // 6. Credentials verified successfully
            log.info("Credentials verified for user '{}'", user.getUsername());
            UserData userData = userService.getUserDetails(user.getId());

            // Enrich UserData with linked farmer profile (National ID, Farmer Member No)
            enrichUserDataWithFarmer(userData, user);

            String token = jwtUtil.generateToken(userData);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            if (!refreshTokenService.saveRefreshToken(refreshToken, user)) {
                response.setMessage("Failed to initialize session. Please try again.");
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return response;
            }

            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .id(userData.getId())
                    .username(userData.getUsername())
                    .mobile(userData.getMobile())
                    .roles(userData.getRoles())
                    .nationalId(userData.getNationalId())
                    .farmerNumber(userData.getFarmerNumber())
                    .fullName(user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : ""))
                    .build();

            response.setMessage("Login Successful");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(authResponse);

        } catch (Exception e) {
            log.error("An error occurred during authentication: {}", e.getMessage(), e);
            response.setMessage("An unexpected error occurred. Please try again.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    /**
     * Resolves an authentication attempt across multiple identifier types:
     * 1. Standard username (e.g. staff, grader, vet, admin, or existing username)
     * 2. Email address
     * 3. Mobile phone number (with or without +254 country code)
     */
    private Optional<User> findExistingUserByIdentifier(String rawInput) {
        if (rawInput.isEmpty()) {
            return Optional.empty();
        }

        // 1. Exact match on username
        Optional<User> userOpt = userRepository.findByUsername(rawInput);
        if (userOpt.isPresent()) {
            return userOpt;
        }

        // 2. Match on email
        userOpt = userRepository.findByEmail(rawInput);
        if (userOpt.isPresent()) {
            return userOpt;
        }

        // 3. Match on mobile in users table
        userOpt = findUserByMobileNumber(rawInput);
        if (userOpt.isPresent()) {
            return userOpt;
        }

        return Optional.empty();
    }

    /**
     * Finds a farmer in the cooperative registry by:
     * 1. National ID Number
     * 2. Farmer Member Number
     * 3. Mobile Phone Number
     */
    private Optional<Farmer> findFarmerByIdentifier(String rawInput) {
        if (rawInput.isEmpty()) {
            return Optional.empty();
        }

        Optional<Farmer> farmerOpt = farmerRepo.findByIdNumber(rawInput);
        if (farmerOpt.isEmpty() && rawInput.matches("^\\d{1,7}$")) {
            try {
                farmerOpt = farmerRepo.getByFarmerNo(Integer.parseInt(rawInput));
            } catch (Exception ignored) {}
        }
        if (farmerOpt.isEmpty()) {
            farmerOpt = findFarmerByMobileNumber(rawInput);
        }
        return farmerOpt;
    }

    private String normalizePhone(String phone) {
        if (phone == null) return null;
        String p = phone.trim().replaceAll("[\\s\\-\\(\\)]", "");
        if (p.startsWith("+254")) {
            return p;
        } else if (p.startsWith("254")) {
            return "+" + p;
        } else if (p.startsWith("0") && p.length() == 10) {
            return "+254" + p.substring(1);
        }
        return p;
    }

    private Optional<User> findUserByMobileNumber(String rawPhone) {
        Optional<User> u = userRepository.findByMobile(rawPhone);
        if (u.isPresent()) return u;
        String norm = normalizePhone(rawPhone);
        if (norm != null) {
            u = userRepository.findByMobile(norm);
            if (u.isPresent()) return u;
            if (norm.startsWith("+254")) {
                u = userRepository.findByMobile("0" + norm.substring(4));
                if (u.isPresent()) return u;
                u = userRepository.findByMobile(norm.substring(1)); // 254...
                if (u.isPresent()) return u;
            }
        }
        return Optional.empty();
    }

    private Optional<Farmer> findFarmerByMobileNumber(String rawPhone) {
        String p1 = rawPhone.trim();
        String norm = normalizePhone(p1);
        String p2 = (norm != null) ? norm : p1;
        String p3 = (norm != null && norm.startsWith("+254")) ? "0" + norm.substring(4) : p1;

        Optional<Farmer> f = farmerRepo.findByMobileVariations(p1, p2, p3);
        if (f.isPresent()) return f;
        return farmerRepo.findByMobileNo(p1);
    }

    private boolean isInitialFarmerPasswordMatch(Farmer farmer, String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            return false;
        }
        String p = rawPassword.trim();
        // 1. National ID as initial PIN
        if (farmer.getIdNumber() != null && !farmer.getIdNumber().isBlank() && p.equalsIgnoreCase(farmer.getIdNumber().trim())) {
            return true;
        }
        // 2. Mobile number as initial password
        if (farmer.getMobileNo() != null && !farmer.getMobileNo().isBlank()) {
            String m = farmer.getMobileNo().trim().replaceAll("[\\s\\-\\(\\)]", "");
            if (p.replaceAll("[\\s\\-\\(\\)]", "").equalsIgnoreCase(m)) {
                return true;
            }
        }
        // 3. Member number as initial PIN
        if (farmer.getFarmerNo() != null && p.equals(farmer.getFarmerNo().toString())) {
            return true;
        }
        // 4. Default cooperative onboarding PIN
        if ("farmer123".equalsIgnoreCase(p)) {
            return true;
        }
        return false;
    }

    private User provisionFarmerUser(Farmer farmer, String rawPassword) {
        log.info("Auto-provisioning ROLE_FARMER account for registered farmer '{}' (No: {}, ID: {})",
                farmer.getUsername(), farmer.getFarmerNo(), farmer.getIdNumber());

        User newUser = new User();
        String username = (farmer.getIdNumber() != null && !farmer.getIdNumber().isBlank())
                ? farmer.getIdNumber().trim()
                : "farmer_" + farmer.getFarmerNo();

        newUser.setUsername(username);
        newUser.setFirstName(farmer.getFirstName() != null ? farmer.getFirstName() : "Farmer");
        newUser.setLastName(farmer.getLastName() != null ? farmer.getLastName() : "Member");
        newUser.setMobile(farmer.getMobileNo() != null ? farmer.getMobileNo().trim() : "");
        String email = (farmer.getFarmerNo() != null)
                ? "farmer" + farmer.getFarmerNo() + "@bahatidairy.co.ke"
                : "farmer_" + System.currentTimeMillis() + "@bahatidairy.co.ke";
        newUser.setEmail(email);
        newUser.setStatus("Active");
        newUser.setIsLoggedIn(0);
        newUser.setPassword(passwordUtil.encode(rawPassword.trim()));
        newUser = userRepository.save(newUser);

        Role farmerRole = roleRepository.findByName("ROLE_FARMER").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_FARMER");
            r.setStatus(1);
            return roleRepository.save(r);
        });

        UserRole userRole = new UserRole();
        userRole.setUser(newUser);
        userRole.setRole(farmerRole);
        userRole.setStatus(1);
        userRoleRepository.save(userRole);

        return newUser;
    }

    private void enrichUserDataWithFarmer(UserData userData, User user) {
        Optional<Farmer> linkedFarmer = Optional.empty();

        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            String uName = user.getUsername().trim();
            linkedFarmer = farmerRepo.findByIdNumber(uName);
            if (linkedFarmer.isEmpty() && uName.matches("^\\d{1,7}$")) {
                try {
                    linkedFarmer = farmerRepo.getByFarmerNo(Integer.parseInt(uName));
                } catch (Exception ignored) {}
            }
            if (linkedFarmer.isEmpty() && uName.startsWith("farmer_")) {
                String num = uName.replace("farmer_", "");
                if (num.matches("^\\d+$")) {
                    try {
                        linkedFarmer = farmerRepo.getByFarmerNo(Integer.parseInt(num));
                    } catch (Exception ignored) {}
                }
            }
            if (linkedFarmer.isEmpty()) {
                linkedFarmer = farmerRepo.findAll().stream()
                        .filter(f -> f.getUsername() != null && f.getUsername().equalsIgnoreCase(uName))
                        .findFirst();
            }
        }

        if (linkedFarmer.isEmpty() && user.getMobile() != null && !user.getMobile().isBlank()) {
            linkedFarmer = findFarmerByMobileNumber(user.getMobile().trim());
        }

        if (linkedFarmer.isPresent()) {
            Farmer f = linkedFarmer.get();
            userData.setNationalId(f.getIdNumber());
            userData.setFarmerNumber(f.getFarmerNo() != null ? f.getFarmerNo().toString() : null);
        } else {
            userData.setNationalId(user.getUsername());
        }
    }


    public EntityResponse<?> logout(String token) {
        AtomicReference<EntityResponse<String>> res = new AtomicReference<>();

        try {
            Optional<RefreshToken> refreshToken = refreshTokenRepo.findByToken(token);

            if (refreshToken.isPresent()) {
                AtomicReference<User> user = new AtomicReference<>(refreshToken.get().getUser());

                user.get().setIsLoggedIn(0);
                user.set(userRepository.save(user.get()));
            }

            refreshToken.ifPresent(refreshTokenRepo::delete);

            res.get().setMessage("User logged out successfully");
            res.get().setMessage("logged out");
            res.get().setStatusCode(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error(e.toString());
            res.get().setMessage("Failed to log out.");
            res.get().setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return res.get();
    }
}
