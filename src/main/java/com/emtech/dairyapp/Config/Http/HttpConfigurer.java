package com.emtech.dairyapp.Config.Http;


import com.emtech.dairyapp.Auth.User.UserService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Log
@Configuration
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity

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
                .cors()
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:4300","http://52.15.152.26:4355", "http://192.168.100.3", "http://18.219.121.50:4500", "http://18.219.121.50:4355"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Origin: *","Access-Control-Allow-Credentials:  Origin, Content-Type, X-Auth-Token, Authorization, Accept"));
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
//        corsConfig.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Origin: *","Access-Control-Allow-Credentials:  Origin, Content-Type, X-Auth-Token"));
//        corsConfig.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin: *","Access-Control-Allow-Credentials:  Origin, Content-Type, X-Auth-Token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }


}
