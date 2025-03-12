package com.emtech.dairyapp.Config.Http;

import com.emtech.dairyapp.Auth.Utilities.CurrentUserContext;
import com.emtech.dairyapp.Auth.Utilities.JWTUtil;
import com.emtech.dairyapp.Auth.Utilities.TokenExpiredException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@Component
@AllArgsConstructor
@Log
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private AuthenticationManager authenticationManager;
    private JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.error(new UnsupportedOperationException("Not supported yet."));
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        System.out.println("tge ehehe ehehe {}"+authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.log(Level.WARNING, "Authorization header missing or invalid format.");
            return Mono.empty();
        }

        String authToken = authHeader.substring(7);
        log.log(Level.INFO, "Extracted Bearer token: {}", authToken);

        try {
            jwtUtil.validateToken(authToken);
        } catch (TokenExpiredException e) {
            log.log(Level.WARNING, "Token expired: " + e.getMessage());
            return Mono.empty();
        }


        String username = jwtUtil.getUsernameFromToken(authToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        CurrentUserContext.setCurrentUserContext(userDetails);

        log.log(Level.INFO, "User retrieved: {}. Authenticating...", userDetails.getUsername());

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, authToken, userDetails.getAuthorities());

        return authenticationManager.authenticate(auth)
                .map(authentication -> (SecurityContext) new SecurityContextImpl(authentication))
                .doOnSuccess(securityContext -> SecurityContextHolder.getContext().setAuthentication(securityContext.getAuthentication()));
    }

//    @Override
//    public Mono<SecurityContext> load(ServerWebExchange swe) {
//        String username;
//
//        ServerHttpRequest request = swe.getRequest();
//        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        String authToken = null;
//        UserDetails userDetails = null;
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            authToken = authHeader.substring(7);
//
//            log.log(Level.INFO, "Bearer token found. Validating it.... token is {} ", authToken);
//
//            if (jwtUtil.validateToken(authToken) && SecurityContextHolder.getContext().getAuthentication() == null) {
//                username = jwtUtil.getUsernameFromToken(authToken);
//                userDetails = userDetailsService.loadUserByUsername(username);
//                CurrentUserContext.setCurrentUserContext(userDetails);
//
//                log.info("user retrieved, The user data is {}. Authenticating ....."+userDetails.getUsername());
//                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                return this.authenticationManager.authenticate(auth)
//                        .doOnSuccess(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication))
//                        .map(SecurityContextImpl::new);
//            } else {
//                log.info("Token invalid. User is not authenticated.");
//                return Mono.empty();
//            }
//
//        } else {
//            log.log(Level.WARNING, "couldn't find bearer string, will ignore the header.");
//            return Mono.empty();
//        }
//    }
}
