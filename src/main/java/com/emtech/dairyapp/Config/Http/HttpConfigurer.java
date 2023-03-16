package com.emtech.dairyapp.Config.Http;


import com.emtech.dairyapp.Auth.User.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;


import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import reactor.core.publisher.Mono;

@Log
//@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class HttpConfigurer {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

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
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.GET, "/swagger-*/**", "/v2/api-docs/**", "/v3/api-docs/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.PUT, "/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/admin/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/admin/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.PUT, "/admin/api/v1/**").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/admin/api/v1/**").permitAll()
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
