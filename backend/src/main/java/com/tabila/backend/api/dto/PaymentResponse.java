package com.tabila.backend.api.dto;

import com.tabila.backend.domain.enums.PaymentProvider;
import com.tabila.backend.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status,
        PaymentProvider provider,
        String phoneNumber,
        String transactionRef,
        Instant createdAt) {
}
