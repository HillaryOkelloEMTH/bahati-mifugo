package com.emtech.dairyapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DairyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(DairyAppApplication.class, args);

        log.info("Up and Running ...");
    }

}
