package com.tabila.backend.api;

import com.tabila.backend.api.dto.CreateOrderRequest;
import com.tabila.backend.api.dto.CreatePaymentRequest;
import com.tabila.backend.api.dto.MenuResponse;
import com.tabila.backend.api.dto.OrderResponse;
import com.tabila.backend.api.dto.PaymentResponse;
import com.tabila.backend.service.RestaurantMvpService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    private final RestaurantMvpService restaurantMvpService;

    public PublicController(RestaurantMvpService restaurantMvpService) {
        this.restaurantMvpService = restaurantMvpService;
    }

    @GetMapping({ "/api/menu", "/menu" })
    public MenuResponse getMenu(@RequestParam Long tableId) {
        return restaurantMvpService.getMenu(tableId);
    }

    @PostMapping({ "/api/orders", "/orders" })
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return restaurantMvpService.createOrder(request);
    }

    @PostMapping({ "/api/payments", "/payments" })
    public PaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return restaurantMvpService.createPayment(request);
    }
}
