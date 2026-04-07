package com.tabila.backend.api.dto;

public record CreatedRestaurantAdminResponse(
                Long userId,
                String email,
                Long restaurantId,
                boolean passwordSent) {
}
