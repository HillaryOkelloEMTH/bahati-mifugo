package com.emtech.dairyapp.Auth.User;

import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.AuthRequest;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.AuthResponse;
import com.emtech.dairyapp.Auth.Data.User.UserData;
import com.emtech.dairyapp.Auth.RefreshToken.RefreshToken;
import com.emtech.dairyapp.Auth.RefreshToken.RefreshTokenRepo;
import com.emtech.dairyapp.Auth.RefreshToken.RefreshTokenService;
import com.emtech.dairyapp.Auth.Role.RoleRepository;
import com.emtech.dairyapp.Auth.UserRole.UserRoleRepository;
import com.emtech.dairyapp.Auth.Utilities.JWTUtil;
import com.emtech.dairyapp.Auth.Utilities.PasswordUtil;
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


    public EntityResponse<?> authenticateUser(AuthRequest authRequest){
        EntityResponse<AuthResponse> response = new EntityResponse<>();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getPassword(), authRequest.getPassword())
            );

            userRepository.findByUsername(authRequest.getUsername().trim()).ifPresentOrElse(user -> {
                if (Objects.equals(user.getStatus(), "Active")){

                    log.info("Encoded Password: {} User Password: {}", passwordUtil.encode(authRequest.getPassword().trim()), user.getPassword());
                    if(passwordUtil.matches(authRequest.getPassword().trim(), user.getPassword())){
                        log.info("Inside password encryption]");
                        UserData userData = userService.getUserDetails(user.getId());

                        String token = jwtUtil.generateToken(userData);
                        String refreshToken = jwtUtil.generateRefreshToken(user);

                        if (!refreshTokenService.saveRefreshToken(refreshToken, user)) {
                            response.setMessage("Invalid username or password");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            return;
                        }

                        AuthResponse authResponse = AuthResponse.builder()
                                .token(token)
                                .refreshToken(refreshToken)
                                .id(userData.getId())
                                .username(userData.getUsername())
                                .mobile(userData.getMobile())
                                .roles(userData.getRoles())
                                .build();

                        response.setMessage("Login Successful");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(authResponse);
                    }else{
                        response.setMessage("Check your password");
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        log.error("Passwords do not match");
                    }
                }else{
                    response.setMessage("Account not found");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    log.error("Account for the provided username is not active {}", authRequest.getUsername());
                }
            }, () -> {
                response.setMessage("User not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                log.error("User with the username not found");
            });
        } catch (InternalAuthenticationServiceException | BadCredentialsException e) {
            log.error("Auth Error {}", e.getMessage());

            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        } catch (Exception e) {
            log.error("An error occurred {}", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return response;
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
