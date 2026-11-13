package com.jane.realestate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RealestateAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealestateAdminApplication.class, args);
    }

}
