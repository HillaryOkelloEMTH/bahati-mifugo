package com.emtech.dairyapp.Auth.User;

import com.emtech.dairyapp.Auth.Role.Role;
import com.emtech.dairyapp.Auth.Role.RoleRepository;
import com.emtech.dairyapp.Auth.Role.RoleService;
import com.emtech.dairyapp.Auth.UserRole.UserRole;
import com.emtech.dairyapp.Auth.UserRole.UserRoleRepository;
import com.emtech.dairyapp.Auth.Utilities.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class AdminData implements CommandLineRunner {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordUtil passwordUtil;


    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    Date modified_on =new Date();
    SimpleDateFormat formatter
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Date date = new Date();

    // getting the object of the Timestamp class
    Timestamp ts = new Timestamp(date.getTime());

    //Add Roles (ROLE_ADMIN and ROLE_USER)
    void addAdminRole() {
        log.info("creating admin role...");

        Role role = new Role();
        role.setName("ROLE_ADMIN");
        role.setAccessRights(roleService.getaccessRights());
        role.setStatus(1);
//            role.setCreationDate(Timestamp.valueOf(formatter.format(ts)));
        roleRepository.save(role);
        log.info("Admin role created.");
    }

    void addRoleIfMissing(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            role.setAccessRights(roleService.getaccessRights());
            role.setStatus(1);
            roleRepository.save(role);
            log.info("Role '{}' created.", roleName);
        }
    }

    void addUserIfMissing(String username, String password, String firstName, String lastName, String email, String mobile, String roleName) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) return;

        var existing = repository.findByUsername(username);
        User user;
        if (existing.isEmpty()) {
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEmail(email);
            user.setMobile(mobile);
            user.setIsLoggedIn(0);
            user.setStatus("Active");
            user.setPassword(passwordUtil.encode(password));
            repository.save(user);

            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setStatus(1);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
            log.info("User '{}' with role '{}' created.", username, roleName);
        } else {
            user = existing.get();
            boolean hasRole = userRoleRepository.findAll().stream()
                    .anyMatch(ur -> ur.getUser().getId().equals(user.getId()) && ur.getRole().getName().equalsIgnoreCase(roleName));
            if (!hasRole) {
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setStatus(1);
                userRole.setRole(role);
                userRoleRepository.save(userRole);
                log.info("Role '{}' added to existing user '{}'.", roleName, username);
            }
        }
    }

    //Default admin records
    void addAdmin() {
        log.info("creating admin user...");
        User user = new User();
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setUsername("admin");
        user.setEmail("emtadmin@gmail.com");
        user.setMobile("0725634469");
        user.setIsLoggedIn(0);
        user.setStatus("Active");
        user.setPassword(passwordUtil.encode("admin"));
        repository.save(user);
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setStatus(1);
        userRole.setRole(adminRole);

        userRoleRepository.save(userRole);
        log.info("admin user created.");
    }

    @Override
    public void run(String... args) throws Exception {
        addRoleIfMissing("ROLE_ADMIN");
        addRoleIfMissing("ROLE_STAFF");
        addRoleIfMissing("MILK_COLLECTOR");
        addRoleIfMissing("ROLE_COLLECTOR");
        addRoleIfMissing("ROLE_GRADER");
        addRoleIfMissing("ROLE_VET");
        addRoleIfMissing("ROLE_FARMER");
        addRoleIfMissing("ROLE_INSURANCE_ADMIN");
        addRoleIfMissing("ROLE_INSURANCE_APPROVER");
        addRoleIfMissing("ROLE_INSURANCE_OFFICER");

        int userCount = repository.countUsers();
        if (userCount < 1) {
            addAdmin();
        }

        // 1. Cooperative Staff User (Role: ROLE_STAFF, strictly cannot collect milk)
        addUserIfMissing("coop_staff", "staff123", "Coop", "Staff", "staff@bahatidairy.co.ke", "0722000001", "ROLE_STAFF");

        // 2. Milk Grader / Collector Users (Role: MILK_COLLECTOR, mapped to routes & collects milk)
        addUserIfMissing("grader_peter", "grader123", "Peter", "Mwangi", "grader@bahatidairy.co.ke", "0722334455", "MILK_COLLECTOR");
        addUserIfMissing("collector_james", "collector123", "James", "Kariuki", "collector@bahatidairy.co.ke", "0722334466", "MILK_COLLECTOR");

        // 3. Veterinary Officer (Role: ROLE_VET, vaccinations, post-mortem, AI muzzle verification)
        addUserIfMissing("vet_sarah", "vet123", "Dr. Sarah", "Wanjiku", "vet@bahatidairy.co.ke", "0722556677", "ROLE_VET");

        // 4. Registered Member / Farmer (Role: ROLE_FARMER, deliveries, slips, digital wallet)
        addUserIfMissing("farmer_john", "farmer123", "John", "Kiprono", "farmer192@bahatidairy.co.ke", "0722778899", "ROLE_FARMER");

        // 5. Insurance Company Users
        addUserIfMissing("ins_officer", "officer123", "Insurance", "Officer", "officer@oldmutual.co.ke", "0722000002", "ROLE_INSURANCE_OFFICER");
        addUserIfMissing("ins_approver", "approver123", "Senior", "Approver", "approver@oldmutual.co.ke", "0722000003", "ROLE_INSURANCE_APPROVER");
    }
}