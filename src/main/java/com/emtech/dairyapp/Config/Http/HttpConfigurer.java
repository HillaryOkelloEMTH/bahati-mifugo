package com.emtech.dairyapp.Config.Http;


import com.emtech.dairyapp.Auth.Role.Role;
import com.emtech.dairyapp.Auth.User.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import reactor.core.publisher.Mono;


import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

@Log
@Configuration
@EnableWebFluxSecurity
public class HttpConfigurer {

    @Autowired
    private UserService userService;

    @Bean
    @Primary
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.exceptionHandling()
                .authenticationEntryPoint((swe, e) -> {
                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    throw new AccessDeniedException(String.format("%s Unauthorized access denied", HttpStatus.UNAUTHORIZED.value()));
                })
                .accessDeniedHandler((swe, e) -> {
                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    throw new AccessDeniedException(String.format("%s Unauthorized access denied", HttpStatus.UNAUTHORIZED.value()));
                })
                .and()
                .requestCache().requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .httpBasic()
                .authenticationManager(authentication -> {
                    if (authentication != null && authentication.getPrincipal() != null && authentication.getCredentials() != null) {
                        log.log(Level.FINE, String.format("Http validate auth [ principal=%s, credentials=%s, data=%s ]", authentication.getPrincipal(), authentication.getCredentials(), authentication));
                        List<Role> roles = this.userService.validateUser(String.valueOf(authentication.getPrincipal()), String.valueOf(authentication.getCredentials()));
                        if (roles != null && !roles.isEmpty()) {
                            return Mono.just(new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), roles.stream().map(Role::getAccessRights).toList().stream().flatMap(Collection::stream).toList().stream().map(s -> new SimpleGrantedAuthority(s.name())).distinct().toList()));
                        } else {
                            return Mono.just(authentication);
                        }
                    } else {
                        log.log(Level.WARNING, String.format("Http validate auth no authenticate [ %s ]", authentication));
                        return Mono.just(new UsernamePasswordAuthenticationToken("", ""));
                    }
                })
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.GET, "/swagger-*/**", "/v2/api-docs/**", "/v3/api-docs/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.PUT, "/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/api/v1/**").permitAll()
                .anyExchange()
                .authenticated();
        return http.build();
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
