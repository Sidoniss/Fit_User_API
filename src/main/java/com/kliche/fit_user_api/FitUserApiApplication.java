package com.kliche.fit_user_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FitUserApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitUserApiApplication.class, args);
    }
}
