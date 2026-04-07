package com.tabila.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRestaurantAdminRequest(
                @NotBlank @Email String email,
                @NotNull Long restaurantId) {
}
