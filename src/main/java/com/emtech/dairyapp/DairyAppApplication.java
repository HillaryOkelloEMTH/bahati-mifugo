package com.emtech.dairyapp;

import com.emtech.dairyapp.Auth.authentication.Privileges.AccessRight;
import com.emtech.dairyapp.Auth.authentication.Privileges.RoleAccessRights;
import com.emtech.dairyapp.Auth.authentication.payload.Role;
import com.emtech.dairyapp.Auth.authentication.payload.User;
import com.emtech.dairyapp.Auth.authentication.repositories.RoleRepository;
import com.emtech.dairyapp.Auth.authentication.repositories.UserRepository;
import com.emtech.dairyapp.Configurations.DepartmentManegement.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@SpringBootApplication
@Slf4j
public class DairyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(DairyAppApplication.class, args);

        log.info("Up and Running ...");
    }
    @Component
    public class AdminData implements CommandLineRunner {

        @Autowired
        private UserRepository repository;


        @Autowired
        private RoleRepository roleRepository;




        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        Date modified_on =new Date();

        public List<RoleAccessRights> adminRights(){

           List<RoleAccessRights> rightsList = new ArrayList<>();

            for (AccessRight s: AccessRight.values()
                 ) {
                RoleAccessRights right = new RoleAccessRights();
                right.setAccessRight(s.name());
                rightsList.add(right);

            }
            return rightsList;

        }


        //Add Roles (ROLE_ADMIN and ROLE_USER)
        void addAdminRole() {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            role.setAccessRights(adminRights());
            roleRepository.save(role);

            Role roleuser = new Role();
            roleuser.setName("ROLE_STAFF");
            role.setAccessRights(adminRights());
            roleRepository.save(roleuser);




        }

        //Default admin records
        void addAdmin() {
            User user = new User();
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            user.setRoles(roles);
            user.setFirstname("admin");
            user.setLastname("admin");
            user.setUsername("admin");
            user.setEmail("emtadmin@gmail.com");
            user.setPhonenumber("0725634469");
            user.setModifiedBy("system");
            user.setCreatedOn(modified_on);
            user.setModifiedOn(modified_on);
            user.setIsAcctActive("Y");
            user.setIsAcctLocked("N");
            user.setDepartment("IT");
            user.setDeleteFlag("N");
            user.setPassword("$2a$10$CQaGCl7cT0DCKwy8i2XaN.8X1jM0" +
                    "9kr6aQgh2DfDV/VQT1SYP3nL6");
            repository.save(user);
        }

        @Override
        public void run(String... args) throws Exception {
            int countusers = repository.countUsers();
            int countroles = roleRepository.countRoles();


            if (countroles < 1) {
                addAdminRole();

            }
            if (countusers < 1) {
                addAdmin();
            }


        }
    }


}
