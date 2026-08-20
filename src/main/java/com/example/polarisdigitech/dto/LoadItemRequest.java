package com.example.polarisdigitech.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoadItemRequest(

        @NotBlank(message = "Item name is required")
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Item name may contain only letters, numbers, hyphen and underscore"
        )
        String name,

        @Min(
                value = 1,
                message = "Item weight must be greater than 0"
        )
        int weight,

        @NotBlank(message = "Item code is required")
        @Pattern(
                regexp = "^[A-Z0-9_]+$",
                message = "Item code may contain only uppercase letters, numbers and underscore"
        )
        String code
) {
}