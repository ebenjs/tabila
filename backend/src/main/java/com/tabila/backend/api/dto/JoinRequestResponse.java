package com.tabila.backend.api.dto;

import com.tabila.backend.domain.enums.JoinRequestStatus;
import java.time.Instant;

public record JoinRequestResponse(
        Long id,
        String restaurantName,
        String contactName,
        String contactEmail,
        String contactPhone,
        String message,
        JoinRequestStatus status,
        Instant createdAt) {
}
