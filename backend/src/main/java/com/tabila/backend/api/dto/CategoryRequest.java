package com.tabila.backend.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(@NotBlank String name, @NotNull Integer displayOrder) {
}
