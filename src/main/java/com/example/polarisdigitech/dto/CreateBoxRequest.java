package com.example.polarisdigitech.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBoxRequest(

        @NotBlank(message = "Transaction reference is required")
        @Size(
                max = 20,
                message = "Transaction reference cannot exceed 20 characters"
        )
        String txref,

        @Min(
                value = 0,
                message = "Battery percentage cannot be below 0"
        )
        @Max(
                value = 100,
                message = "Battery percentage cannot exceed 100"
        )
        int batteryPercentage
) {
}