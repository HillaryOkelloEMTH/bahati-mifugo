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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

//@Configuration
//@Log
public class WebSecurityConfig {
//    @Autowired
//    private UserService userService;
//        @Bean
//    @Primary
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//       http
//               .exceptionHandling()
//               .accessDeniedHandler((request, response, accessDeniedException) -> {
//                   response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                   throw new AccessDeniedException(String.format("%s Unauthorized access denied", HttpStatus.UNAUTHORIZED.value()));
//               })
//               .and()
//               .requestCache()
//               .and()
//               .csrf()
//               .disable()
//               .formLogin()
//               .disable()
//               .logout().disable()
//               .httpBasic()
//               .and()
//               .authorizeRequests()
//               .antMatchers("/swagger-*/**", "/v2/api-docs/**", "/v3/api-docs/**").permitAll();
//    }
//
//    AuthenticationManager userAuthentication(){
//            return authentication -> {
//                if (authentication != null && authentication.getPrincipal() != null && authentication.getCredentials() != null){
//                    log.log(Level.FINE, String.format("Http validate auth [ principal=%s, credentials=%s, data=%s ]", authentication.getPrincipal(), authentication.getCredentials(), authentication));
//
//                    List<Role> roles = this.userService.validateUser(String.valueOf(authentication.getPrincipal()), String.valueOf(authentication.getCredentials()));
//
//                    if (roles != null && !roles.isEmpty()) {
//                        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), roles.stream().map(Role::getAccessRights).toList().stream().flatMap(Collection::stream).toList().stream().map(s -> new SimpleGrantedAuthority(s.name())).distinct().toList());
//                    } else {
//                        return authentication;
//                    }
//                }else {
//                    log.log(Level.WARNING, String.format("Http validate auth no authenticate [ %s ]", authentication));
//                    return new UsernamePasswordAuthenticationToken("", "");
//                }
//            };
//    };
//
//        ServerAuthenticationEntryPoint serverAuthenticationEntryPoint (){
//            return (swe, e) -> {
//                swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                throw new AccessDeniedException(String.format("%s Unauthorized access denied", HttpStatus.UNAUTHORIZED.value()));
//            };
//        }




}
