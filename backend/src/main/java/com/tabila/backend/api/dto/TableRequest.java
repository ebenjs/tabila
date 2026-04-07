package com.tabila.backend.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TableRequest(@NotBlank String name, Boolean active) {
}
