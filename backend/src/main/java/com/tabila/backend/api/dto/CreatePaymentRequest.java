package com.tabila.backend.api.dto;

import com.tabila.backend.domain.enums.PaymentProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull Long orderId,
        @NotNull PaymentProvider provider,
        @NotBlank String phoneNumber) {
}
