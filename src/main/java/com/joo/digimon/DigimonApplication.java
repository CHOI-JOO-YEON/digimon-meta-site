package com.joo.digimon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DigimonApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigimonApplication.class, args);
    }

}
