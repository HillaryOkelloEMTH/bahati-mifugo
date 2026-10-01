package com.emtech.dairyapp.Auth.Role;

import com.emtech.dairyapp.Auth.UserRole.UserRole;
import com.emtech.dairyapp.Auth.UserRole.UserRoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.event.EventListener;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleRepositoryTest {
    @Autowired
    private UserRoleRepository roleRepository;

    @Autowired
    private RoleRepository roleRepo;

    @Test
    @Order(1)
    @DisplayName("Test 1: Count the number of roles")
    public void countRoles() {
        List<Role> roles = roleRepo.findAll();
        Assertions.assertThat(roles.size()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Count Admin Roles")
    public void countAdmins(){
        Optional<Role> role = roleRepo.findByName("ROLE_ADMIN");
        Assertions.assertThat(role).isPresent();
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Find admin role and user")
    @Rollback(value = false)
    public void findAllTest() {
        List<UserRole> roles = roleRepository.findAll();
        Assertions.assertThat(roles.size()).isGreaterThan(0);
    }
}