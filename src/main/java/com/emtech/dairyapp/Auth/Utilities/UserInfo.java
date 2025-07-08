package com.emtech.dairyapp.Auth.Utilities;

import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Auth.User.UserRepository;
import com.emtech.dairyapp.Auth.UserRole.UserRole;
import com.emtech.dairyapp.Auth.UserRole.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class UserInfo {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepo;

    @Autowired
    public UserInfo(UserRepository userRepository, UserRoleRepository userRoleRepo) {
        this.userRepository = userRepository;
        this.userRoleRepo = userRoleRepo;
    }

//    public static String username() {
//        String username = "";
//        if (CurrentUserContext.getCurrentUserContext() != null) {
//            return CurrentUserContext.getCurrentUserContext().getUsername();
//        } else {
//            return username;
//        }
//    }


    public static String username() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                return ((UserDetails) authentication.getPrincipal()).getUsername();
            } else {
                return authentication.getPrincipal().toString();
            }
        }

        return "UNKNOWN_USER";
    }

    public String userRole() {
        Optional<User> optionalUser = userRepository.findByUsername(UserInfo.username());
        if (optionalUser.isEmpty()) {
            return "";
        }

        User user = optionalUser.get();

        Optional<UserRole> optional = userRoleRepo.findByUser(user);

        if (optional.isEmpty()) {
            return "";
        }

        return optional.get().getRole().getName();
    }

    public static User user() {
        return reactiveUser().blockOptional().orElse(new User());
    }

    public static Mono<User> reactiveUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserDetails)
                .map(principal -> {
                    UserDetails userDetails = (UserDetails) principal;
                    System.out.println("Current User Data: "+userDetails);
                    return new User(userDetails.getUsername());
                })
                .switchIfEmpty(Mono.just(new User()));
    }
}
