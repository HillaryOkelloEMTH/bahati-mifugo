package com.emtech.dairyapp.Config.Http;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
//        return Mono.justOrEmpty(swe.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
//                .filter(authHeader -> authHeader.startsWith("Bearer "))
//                .flatMap(authHeader -> {
//                    String authToken = "";
//                    if(authHeader != null && !authHeader.isEmpty()) {
//                         authToken = authHeader.substring(7);
//                    }else{
//                        authToken = "";
//                    }
//
//                    Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
//
//                    log.log(Level.WARNING, String.format("Auth [ %s ]", auth));
//
//
//                    return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
//                });

        ServerHttpRequest request = swe.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String authToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authToken = authHeader.substring(7);

            log.log(Level.WARNING, String.format("Bearer token found and is valid."));
        }else {
            log.log(Level.WARNING, String.format("couldn't find bearer string, will ignore the header."));
        }
        if (authToken != null) {
            Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);

            log.log(Level.WARNING, String.format("Auth [ %s ] ", auth));

            return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
        } else {
            return Mono.empty();
        }
    }
}
