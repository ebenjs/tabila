package com.tabila.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateJoinRequestRequest(
        @NotBlank String restaurantName,
        @NotBlank String contactName,
        @Email @NotBlank String contactEmail,
        @NotBlank String contactPhone,
        String message) {
}
