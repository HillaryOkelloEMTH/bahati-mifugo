package com.emtech.dairyapp.Config.Http;



import com.emtech.dairyapp.Auth.Role.Role;
import com.emtech.dairyapp.Auth.User.UserService;
import com.emtech.dairyapp.Auth.Utilities.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Component
@Log
public class AuthenticationManager implements ReactiveAuthenticationManager {
    @Lazy
    private final UserService userService;

    public AuthenticationManager(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.log(Level.FINE, String.format("Http validate auth [ Principal=%s ]",  authentication.getPrincipal()));

        if (authentication.getPrincipal() != null) {
            log.log(Level.FINE, String.format("Http validate auth [ Principal=%s, ]",  authentication.getPrincipal()));

            String authToken = authentication.getCredentials().toString();
            if (authToken.isEmpty()) {
                return Mono.just(new UsernamePasswordAuthenticationToken("", ""));
            }

//            String username = jwtUtil.getUsernameFromToken(authToken);

            List<Role> roles = this.userService.validateUser(authToken);
            if (roles != null && !roles.isEmpty()) {
                log.log(Level.WARNING, String.format("Authenticated user roles [ %s ] ", roles));
                return Mono.just(new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                        authentication.getCredentials(),
                        roles.stream().map(Role::getAccessRights)
                                .toList().stream().flatMap(Collection::stream)
                                .toList().stream().map(s -> new SimpleGrantedAuthority(s.name())).distinct()
                                .collect(Collectors.toList())));
            } else {
                return Mono.just(authentication);
            }
        } else {
            log.log(Level.WARNING, String.format("Http validate auth no authenticate [ %s ]", authentication));
            return Mono.just(new UsernamePasswordAuthenticationToken("", ""));
        }
    }
}
