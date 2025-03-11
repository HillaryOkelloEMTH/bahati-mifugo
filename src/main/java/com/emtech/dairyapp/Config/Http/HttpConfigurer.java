package com.emtech.dairyapp.Config.Http;


import com.emtech.dairyapp.Auth.User.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
public class HttpConfigurer {

    @Autowired
    private UserService userService;

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    @Primary
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchange -> exchange.pathMatchers(EndPoints.allowedUrls.toArray(String[]::new)).permitAll()
                        .anyExchange().authenticated())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((swe, e) -> {
                            swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            swe.getResponse().getHeaders().add("Content-Type", "application/json");

                            Map<String, Object> errorResponse = Map.of(
                                    "message", "Not Authorized. Request Blocked.",
                                    "status", 401,
                                    "error", "Unauthorized"
                            );

                            log.info("Not Authorized. Request Blocked. :: {}", HttpStatus.UNAUTHORIZED);
                            byte[] jsonResponse = null;
                            try {
                                jsonResponse = objectMapper.writeValueAsBytes(errorResponse);
                            } catch (JsonProcessingException ex) {
                                throw new RuntimeException(ex);
                            }

                            return swe.getResponse().writeWith(Mono.just(swe.getResponse().bufferFactory().wrap(jsonResponse)));
                        })
                        .accessDeniedHandler((swe, e) -> {
                            swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            swe.getResponse().getHeaders().add("Content-Type", "application/json");

                            Map<String, Object> errorResponse = Map.of(
                                    "message", "Access Denied. Limited Rights.",
                                    "status", 403,
                                    "error", "Forbidden"
                            );

                            log.info("Access Denied. Doesn't have required rights. ::{}", HttpStatus.FORBIDDEN);
                            byte[] jsonResponse = null;
                            try {
                                jsonResponse = objectMapper.writeValueAsBytes(errorResponse);
                            } catch (JsonProcessingException ex) {
                                throw new RuntimeException(ex);
                            }

                            return swe.getResponse().writeWith(Mono.just(swe.getResponse().bufferFactory().wrap(jsonResponse)));
                        })
                )
                .securityContextRepository(securityContextRepository)
                .authenticationManager(authenticationManager)
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .logout(ServerHttpSecurity.LogoutSpec::disable);
        return http.build();
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:4300","http://52.15.152.26:4355", "http://192.168.100.3", "http://18.219.121.50:4500", "http://18.219.121.50:4355"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
        configuration.setExposedHeaders(List.of("X-Get-Header"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:4300","http://52.15.152.26:4355", "http://192.168.100.3", "http://18.219.121.50:4500", "http://18.219.121.50:4355"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
        corsConfig.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin: *","Access-Control-Allow-Credentials:  Origin, Content-Type, X-Auth-Token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}
