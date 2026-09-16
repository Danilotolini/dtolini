package com.dstolini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DsToliniApplication {

    public static void main(String[] args) {
        SpringApplication.run(DsToliniApplication.class, args);
    }
}
