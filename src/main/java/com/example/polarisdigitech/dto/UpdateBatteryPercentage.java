package com.example.polarisdigitech.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateBatteryPercentage(

        @NotNull(message = "Battery percentage is required")
        @Min(
                value = 0,
                message = "Battery percentage cannot be below 0"
        )
        @Max(
                value = 100,
                message = "Battery percentage cannot exceed 100"
        )
        Integer batteryPercentage
) {
}