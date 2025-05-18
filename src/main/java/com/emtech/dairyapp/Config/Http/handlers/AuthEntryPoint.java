package com.emtech.dairyapp.Config.Http.handlers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class AuthEntryPoint implements ServerAuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Mono<Void> commence(ServerWebExchange swe, AuthenticationException ex) {

        swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        swe.getResponse().getHeaders().add("Content-Type", "application/json");

        Map<String, Object> errorResponse = Map.of(
                "message", "Not Authorized. Request Blocked.",
                "status", 401,
                "error", "Unauthorized"
        );

        log.info("Not Authorized. Request Blocked. :: {}", HttpStatus.UNAUTHORIZED);
        log.info("Blocked Request From Address {} and Request URI :: {}", swe.getRequest().getRemoteAddress(), swe.getRequest().getURI());
        byte[] jsonResponse = null;
        try {
            jsonResponse = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize error response. ");
            jsonResponse = "{'status': 'Unauthorized', 'code': '401'}".getBytes();
        }

        return swe.getResponse().writeWith(Mono.just(swe.getResponse().bufferFactory().wrap(jsonResponse)));
    }
}
