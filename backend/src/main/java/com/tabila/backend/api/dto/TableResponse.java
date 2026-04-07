package com.tabila.backend.api.dto;

public record TableResponse(Long id, String name, String qrCodeUrl, boolean active) {
}
