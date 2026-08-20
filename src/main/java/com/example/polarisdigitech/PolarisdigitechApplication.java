package com.example.polarisdigitech;

import com.example.polarisdigitech.config.BoxProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BoxProperties.class)
public class PolarisdigitechApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolarisdigitechApplication.class, args);
    }
}