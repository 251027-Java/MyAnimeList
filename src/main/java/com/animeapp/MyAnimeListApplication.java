package com.animeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.animeapp") // Example
@EnableJpaRepositories(basePackages = "com.animeapp") // Example
public class MyAnimeListApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAnimeListApplication.class, args);
    }

}
