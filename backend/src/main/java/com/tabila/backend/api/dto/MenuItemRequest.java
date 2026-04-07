package com.tabila.backend.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MenuItemRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @DecimalMin("0") BigDecimal price,
        String imageUrl,
        @NotNull Long categoryId,
        Boolean available) {
}
