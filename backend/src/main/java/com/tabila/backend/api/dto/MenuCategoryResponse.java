package com.tabila.backend.api.dto;

import java.util.List;

public record MenuCategoryResponse(Long id, String name, Integer displayOrder, List<MenuItemResponse> items) {
}
