package com.emtech.dairyapp.Auth.Utilities;

import com.emtech.dairyapp.Auth.User.UserRepository;
import com.emtech.dairyapp.Auth.UserRole.UserRole;
import com.emtech.dairyapp.Auth.UserRole.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsConfig implements UserDetailsService{
        private final UserRoleRepository userRoleRepository;
        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            log.info("Getting user by the username {}", username);
            return userRepository.findByUsername(username)
                    .map(user -> {
                        List<UserRole> userRoles = userRoleRepository.findAllByUser(user);
                        return new User(
                                user.getUsername(),
                                user.getPassword(),
                                userRoles.get(0).getAuthorities());
                    })
                    .orElseThrow(() -> new UsernameNotFoundException("User not with username " + username+" not found"));
        }

}
