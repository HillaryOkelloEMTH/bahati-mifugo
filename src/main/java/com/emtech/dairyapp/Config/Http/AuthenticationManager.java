package com.emtech.dairyapp.Config.Http;


import com.emtech.dairyapp.Auth.Role.Role;
import com.emtech.dairyapp.Auth.User.UserService;
import com.emtech.dairyapp.Auth.Utilities.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
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
@AllArgsConstructor
@Log
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private UserService userService;

    private JWTUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
//        String authToken = authentication.getCredentials().toString();
//        String username = jwtUtil.getUsernameFromToken(authToken);
//        return Mono.just(jwtUtil.validateToken(authToken))
//                .filter(valid -> valid)
//                .switchIfEmpty(Mono.empty())
//                .map(valid -> {
//                    Claims claims = jwtUtil.getAllClaimsFromToken(authToken);
//                    List<String> rolesMap = claims.get("role", List.class);
//                    return new UsernamePasswordAuthenticationToken(
//                            username,
//                            null,
//                            rolesMap.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
//                    );
//                });
        if (authentication != null && authentication.getCredentials() != null) {
            log.log(Level.FINE, String.format("Http validate auth [credentials=%s, data=%s ]",  authentication.getCredentials(), authentication));

            String authToken = authentication.getCredentials().toString();

            String username = jwtUtil.getUsernameFromToken(authToken);

            List<Role> roles = this.userService.validateUser(username);
            if (roles != null && !roles.isEmpty()) {
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
