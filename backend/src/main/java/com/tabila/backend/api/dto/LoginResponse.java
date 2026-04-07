package com.tabila.backend.api.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        String role) {
}
