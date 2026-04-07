package com.tabila.backend.api.dto;

public record RestaurantConfigResponse(
        Long id,
        String name,
        String welcomeMessage,
        String backgroundColor,
        String accentColor,
        String currency) {
}
