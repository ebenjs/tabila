package com.tabila.backend.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RestaurantConfigRequest(
        @NotBlank String name,
        @NotBlank String welcomeMessage,
        @NotBlank @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Format attendu: #RRGGBB") String backgroundColor,
        @NotBlank @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Format attendu: #RRGGBB") String accentColor) {
}
