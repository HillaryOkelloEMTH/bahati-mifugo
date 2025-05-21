package com.emtech.dairyapp;


import com.emtech.dairyapp.Auth.Role.Role;
import com.emtech.dairyapp.Auth.Role.RoleRepository;
import com.emtech.dairyapp.Auth.Role.RoleService;
import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Auth.User.UserRepository;
import com.emtech.dairyapp.Auth.UserRole.UserRole;
import com.emtech.dairyapp.Auth.UserRole.UserRoleRepository;
import com.emtech.dairyapp.Auth.Utilities.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@SpringBootApplication
@Slf4j
public class DairyAppApplication {
    //test comment
    public static void main(String[] args) {
        System.setProperty("user.timezone", "Africa/Nairobi");
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Nairobi"));

        SpringApplication.run(DairyAppApplication.class, args);
        Date currentDate = Calendar.getInstance(TimeZone.getDefault()).getTime();
        log.info("Current Local Date for Africa/Nairobi Zone is :: {} ::", currentDate);

        log.info("Up and Running ...");
    }
}
