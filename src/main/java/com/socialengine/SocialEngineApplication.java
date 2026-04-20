package com.socialengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocialEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialEngineApplication.class, args);
    }
}
