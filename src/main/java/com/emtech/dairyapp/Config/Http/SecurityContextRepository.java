package com.emtech.dairyapp.Config.Http;

import com.emtech.dairyapp.Auth.Utilities.CurrentUserContext;
import com.emtech.dairyapp.Auth.Utilities.JWTUtil;
import com.emtech.dairyapp.Auth.Utilities.TokenExpiredException;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
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
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@Component
@AllArgsConstructor
@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private AuthenticationManager authenticationManager;
    private JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    ServerSecurityContextRepository serverSecurityContextRepository;
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        exchange.getAttributes().put(SecurityContext.class.getName(),context);
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Authorization header missing or invalid format.");
            return Mono.empty();
        }

        String authToken = authHeader.substring(7);
        log.info("Extracted Bearer token: {}", authToken);

        try {
            jwtUtil.validateToken(authToken);
        } catch (TokenExpiredException e) {
            log.info("Token expired: {}", e.getMessage());
            return Mono.empty();
        } catch (JwtException e) {
            log.info( "Authorization header missing or invalid format");
            return Mono.empty();
        }

        String username = jwtUtil.getUsernameFromToken(authToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        CurrentUserContext.setCurrentUserContext(userDetails);

        log.info("User retrieved: {}. Authenticating ...", userDetails.getUsername());

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, authToken, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return authenticationManager.authenticate(auth)
                .map(SecurityContextImpl::new);
    }
}
