package com.findit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FindItApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindItApiApplication.class, args);
    }
}
