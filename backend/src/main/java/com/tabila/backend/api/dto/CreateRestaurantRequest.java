package com.tabila.backend.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantRequest(
        @NotBlank(message = "Le nom du restaurant est requis") String name) {
}
