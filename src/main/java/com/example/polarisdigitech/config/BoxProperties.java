package com.example.polarisdigitech.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "box")
public record BoxProperties(
        int maxWeight
) {
}