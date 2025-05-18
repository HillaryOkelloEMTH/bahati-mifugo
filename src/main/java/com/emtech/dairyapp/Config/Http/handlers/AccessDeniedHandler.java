package com.emtech.dairyapp.Config.Http.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;


@Slf4j
public class AccessDeniedHandler implements ServerAccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Mono<Void> handle(ServerWebExchange swe, AccessDeniedException denied) {
        swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        swe.getResponse().getHeaders().add("Content-Type", "application/json");

        Map<String, Object> errorResponse = Map.of(
                "message", "Access Denied. Limited Rights.",
                "status", 403,
                "error", "Forbidden"
        );

        log.info("Access Denied. Doesn't have required rights. ::{}", HttpStatus.FORBIDDEN);
        log.info("Request from remote add {}, and request url {} blocked.", swe.getRequest().getRemoteAddress(), swe.getRequest().getURI());
        byte[] jsonResponse = null;
        try {
            jsonResponse = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize exception. {}", ex.getMessage());
            jsonResponse = "{'error': 'Access Denied', 'code':'403'}".getBytes();
        }

        return swe.getResponse().writeWith(Mono.just(swe.getResponse().bufferFactory().wrap(jsonResponse)));
    }
}
