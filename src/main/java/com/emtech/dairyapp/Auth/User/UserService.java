package com.emtech.dairyapp.Auth.User;

import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.AuthRequest;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.AuthResponse;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.UserResponse;
import com.emtech.dairyapp.Auth.Data.Role.RoleAccessRights;
import com.emtech.dairyapp.Auth.Data.User.UserData;
import com.emtech.dairyapp.Auth.Data.User.UserRoleData;
import com.emtech.dairyapp.Auth.Role.Role;
import com.emtech.dairyapp.Auth.Role.RoleRepository;
import com.emtech.dairyapp.Auth.UserRole.UserRole;
import com.emtech.dairyapp.Auth.UserRole.UserRoleRepository;
import com.emtech.dairyapp.Auth.Utilities.JWTUtil;
import com.emtech.dairyapp.Auth.Utilities.PasswordUtil;
import com.emtech.dairyapp.Auth.Utilities.SendCredentialToMail;
import com.emtech.dairyapp.Auth.Utilities.ToolKit;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Service
public class UserService {
//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordUtil passwordUtil;

    @Value("${jwt.password.encoder.secret}")
    private String passwordSecret;



    public List<Role> validateUser(@NonNull String username) {
        List<Role> roles = new ArrayList<>();

        this.userRepository.findByUsername(username.trim()).ifPresent(user -> {
            if (user.getStatus() == "Active") {
                roles.addAll(this.userRoles(user, true));
            }
        });

        return roles;
    }

    public List<Role> userRoles(@NonNull User user, boolean activeOnly) {
        if (activeOnly) {
            return this.userRoleRepository.findAllByUser(user).stream().map(UserRole::getRole).collect(Collectors.toList());
        } else {
            return this.userRoleRepository.findAllByUserAndStatus(user, 1).stream().map(UserRole::getRole).collect(Collectors.toList());
        }
    }

    public boolean createUser(@NonNull String userName, @NonNull String firstName, @NonNull String lastName, @NonNull String email, @NonNull String mobile, @NonNull Long roleId){
        AtomicBoolean res = new AtomicBoolean();

        this.userRepository.findByUsername(userName).ifPresentOrElse(user -> {
            /* todo:: Username already exists  */
        }, () -> {
            this.userRepository.findByEmail(email).ifPresentOrElse(user -> {
                /* todo:: Email already exists  */

            }, () -> {
                this.roleRepository.findById(roleId).ifPresentOrElse(role -> {
                    if(role.getStatus().compareTo(1) == 0){
                        AtomicReference<User> user = new AtomicReference<>(new User());
                        user.get().setUsername(userName.trim());
                        user.get().setFirstName(firstName);
                        user.get().setLastName(lastName.trim());
                        user.get().setEmail(email.trim());
                        user.get().setMobile(mobile);
                        user.get().setStatus("Active");
                        user.get().setIsLoggedIn(0);

                        ToolKit tk = new ToolKit();

                        String userPassword = tk.generatePassword();

                        user.get().setPassword(passwordUtil.encode(userPassword));

                        user.set(this.userRepository.save(user.get()));

                        log.log(Level.INFO, String.format("User created [ %s ]", user.get()));

                        log.log(Level.INFO, String.format("Role Details [ %s ]", role));

                        if (this.assignRole(user.get(), role, true)) {
                            log.log(Level.INFO, String.format("User assigned role [ %s ]", user.get()));
                        }

                        try {
                            SendCredentialToMail sm = new SendCredentialToMail();

                            log.log(Level.INFO, String.format("User Email [ %s ]", user.get().getEmail()));

                            sm.sendMail(user.get().getEmail(), user.get().getUsername(), userPassword);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                        res.set(true);
                    }else {
                        /* todo:: Role is not active  */
                    }
                }, () -> {
                    /* todo:: role not found  */
                });
            });
        });

        return res.get();
    }

    public AuthResponse authenticateUser(@NonNull String username, @NonNull String password){
        AtomicReference<AuthResponse> response = new AtomicReference<>();

        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            log.log(Level.INFO, String.format("User Credentials [credentials=%s]", user));

            if (Objects.equals(user.getStatus(), "Active")){
                log.log(Level.INFO, String.format("User Credentials [credentials=%s]", user));
                log.log(Level.INFO, String.format("Encode Password [credentials=%s] ", passwordUtil.matches(password, user.getPassword())));
                if(passwordUtil.matches(password, user.getPassword())){
                    log.log(Level.INFO, String.format("Inside password encryption]"));
                    UserData userData = getUserDetails(user.getId());

                    log.log(Level.INFO, String.format("User Data Details [ %s ]", userData.toString()));

                    String token = jwtUtil.generateToken(userData);

                    AuthResponse authResponse = AuthResponse.builder()
                            .token(token)
                            .id(userData.getId())
                            .username(userData.getUsername())
                            .mobile(userData.getMobile())
                            .roles(userData.getRoles())
                            .build();

                    response.set(authResponse);
                }else{
                    /* todo:: Provided an invalid password  */
                }

            }else{
                /* todo:: User account not active  */
            }
        }, () -> {
            /* todo:: user not found  */
        });

        return response.get();
    }

    public boolean updateUser(@NonNull Long userId, @NonNull String userName, @NonNull String firstName, @NonNull String lastName, @NonNull String email, @NonNull Long roleId){
        AtomicBoolean res = new AtomicBoolean();

        this.userRepository.findById(userId).ifPresentOrElse(userData -> {
            AtomicReference<User> user = new AtomicReference<>(userData);
            user.get().setUsername(userName.trim());
            user.get().setFirstName(firstName);
            user.get().setLastName(lastName.trim());
            user.get().setEmail(email.trim());
            user.get().setStatus("Active");
            user.set(this.userRepository.save(user.get()));

            res.set(true);
        }, () -> {
            /* todo:: User not found  */
        });

        return res.get();
    }

    public boolean updateUserStatus(@NonNull Long userId, @NonNull String status){
        AtomicBoolean res = new AtomicBoolean();

        this.userRepository.findById(userId).ifPresentOrElse(userData -> {
            AtomicReference<User> user = new AtomicReference<>(userData);
            user.get().setStatus(status);

            user.set(this.userRepository.save(user.get()));

            res.set(true);
        }, () -> {
            /* todo:: User not found  */
        });

        return res.get();
    }

    public boolean logoutUser(@NonNull Long userId, @NonNull Integer status){
        AtomicBoolean res = new AtomicBoolean();

        this.userRepository.findById(userId).ifPresentOrElse(userData -> {
            AtomicReference<User> user = new AtomicReference<>(userData);
            user.get().setIsLoggedIn(status);

            user.set(this.userRepository.save(user.get()));

            res.set(true);
        }, () -> {
            /* todo:: User not found  */
        });

        return res.get();
    }


    public boolean assignRole(@NonNull User user, @NonNull Role role, boolean activate){
        AtomicBoolean res = new AtomicBoolean();

        this.userRepository.findById(user.getId()).ifPresentOrElse(userData -> {
            if(Objects.equals(userData.getStatus(), "Active")){
                this.roleRepository.findById(role.getId()).ifPresentOrElse(myRole -> {
                    if(myRole.getStatus().compareTo(1) == 0){
                        userRoleRepository.findByUserAndRole(userData, myRole).ifPresentOrElse(ur -> {
                            AtomicReference<UserRole> userRole = new AtomicReference<>(ur);
                            if (activate) {
                                userRole.get().setStatus(1); /* 1 - activate */
                            } else {
                                userRole.get().setStatus(2); /* 2 - disabled */
                            }

                            userRole.set(this.userRoleRepository.save(userRole.get()));
                            log.log(Level.INFO, String.format("User role deactivated [ %s ]", userRole.get()));
                            res.set(true);
                        }, () -> {
                            if (activate) {
                                AtomicReference<UserRole> userRole = new AtomicReference<>(new UserRole());
                                userRole.get().setUser(userData);
                                userRole.get().setRole(myRole);
                                userRole.get().setStatus(1);
                                userRole.set(this.userRoleRepository.save(userRole.get()));
                                log.log(Level.INFO, String.format("User role created [ %s ]", userRole.get()));

                                res.set(true);
                            }
                        });
                    }else {
                        /* todo:: role not active  */
                    }
                }, () -> {
                    /* todo:: role not found  */
                });
            }else{
                /* todo:: User is not active  */
            }
        }, () -> {
            /* todo:: user not found  */
        });

        return res.get();
    }

    public UserResponse getAllUsers(){
        AtomicReference<UserResponse> response = new AtomicReference<>();

        List<UserData> usersResponse = new ArrayList<>();
        List<User> users = this.userRepository.findAll();

        if(users != null && !users.isEmpty()){
            users.forEach(user -> {
                UserData userData = UserData.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .mobile(user.getMobile())
                        .status(user.getStatus())
                        .creationDate(user.getCreationDate())
                        .updateDate(user.getUpdateDate())
                        .isLoggedIn(user.getIsLoggedIn())
                        .build();

                List<UserRoleData> roles = new ArrayList<>();

                List<UserRole> userRoles = this.userRoleRepository.findAllByUser(user);
                if(userRoles != null && !userRoles.isEmpty()){
                    userRoles.forEach(userRole -> {
                        UserRoleData userRoleData = UserRoleData.builder()
                                .name(userRole.getRole().getName())
                                .build();

                        List<RoleAccessRights> accessRights = new ArrayList<>();
                        if(userRole.getRole().getStatus() !=  null && !userRole.getRole().getAccessRights().isEmpty()){
                            userRole.getRole().getAccessRights().forEach(accessRight -> {
                                accessRights.add(RoleAccessRights.builder().name(accessRight.getName()).accessRights(accessRight).build());
                            });
                        }

                        userRoleData.setAccessRights(accessRights);

                        roles.add(userRoleData);

                        userData.setRoles(roles);
                    });
                }

                usersResponse.add(userData);
            });

            response.set(UserResponse.builder().userData(usersResponse).build());
        }

        return response.get();
    }

    public UserData getUserDetails(@NonNull Long userId){
        AtomicReference<UserData> response = new AtomicReference<>();

        this.userRepository.findById(userId).ifPresentOrElse(user -> {
            UserData data = UserData.builder()
                    .id(userId)
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .mobile(user.getMobile())
                    .status(user.getStatus())
                    .creationDate(user.getCreationDate())
                    .updateDate(user.getUpdateDate())
                    .isLoggedIn(user.getIsLoggedIn())
                    .build();

            List<UserRoleData> roles = new ArrayList<>();

            List<UserRole> userRoles = this.userRoleRepository.findAllByUser(user);

            if(userRoles != null && !userRoles.isEmpty()){
                userRoles.forEach(userRole -> {
                    UserRoleData userRoleData = UserRoleData.builder()
                            .name(userRole.getRole().getName())
                            .build();

                    List<RoleAccessRights> accessRights = new ArrayList<>();
                    if(userRole.getRole().getStatus() !=  null && !userRole.getRole().getAccessRights().isEmpty()){
                        userRole.getRole().getAccessRights().forEach(accessRight -> {
                            accessRights.add(RoleAccessRights.builder().name(accessRight.getName()).accessRights(accessRight).build());
                        });
                    }

                    userRoleData.setAccessRights(accessRights);

                    roles.add(userRoleData);

                    data.setRoles(roles);
                });
            }

            response.set(data);
        }, () -> {
            /* todo:: user not found  */
        });

        return response.get();
    }

    public UserResponse getUsersByStatus(@NonNull String status){
        AtomicReference<UserResponse> response = new AtomicReference<>();

        List<UserData> usersResponse = new ArrayList<>();
        List<User> users = this.userRepository.findByStatus(status);

        if(users != null && !users.isEmpty()){
            users.forEach(user -> {
                UserData userData = UserData.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .mobile(user.getMobile())
                        .status(user.getStatus())
                        .creationDate(user.getCreationDate())
                        .updateDate(user.getUpdateDate())
                        .isLoggedIn(user.getIsLoggedIn())
                        .build();

                List<UserRoleData> roles = new ArrayList<>();

                List<UserRole> userRoles = this.userRoleRepository.findAllByUser(user);
                if(userRoles != null && !userRoles.isEmpty()){
                    userRoles.forEach(userRole -> {
                        UserRoleData userRoleData = UserRoleData.builder()
                                .name(userRole.getRole().getName())
                                .build();

                        List<RoleAccessRights> accessRights = new ArrayList<>();
                        if(userRole.getRole().getStatus() !=  null && !userRole.getRole().getAccessRights().isEmpty()){
                            userRole.getRole().getAccessRights().forEach(accessRight -> {
                                accessRights.add(RoleAccessRights.builder().name(accessRight.getName()).accessRights(accessRight).build());
                            });
                        }

                        userRoleData.setAccessRights(accessRights);

                        roles.add(userRoleData);

                        userData.setRoles(roles);
                    });
                }

                usersResponse.add(userData);
            });

            response.set(UserResponse.builder().userData(usersResponse).build());
        }

        return response.get();
    }
}
