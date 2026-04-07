package com.tabila.backend.api.dto;

import java.util.List;

public record MenuResponse(
        Long tableId,
        String tableName,
        String restaurantName,
        String welcomeMessage,
        String backgroundColor,
        String accentColor,
        List<MenuCategoryResponse> categories) {
}
