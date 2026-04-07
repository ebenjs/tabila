package com.tabila.backend.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record PaymentsDashboardResponse(BigDecimal totalRevenue, List<PaymentResponse> payments) {
}
