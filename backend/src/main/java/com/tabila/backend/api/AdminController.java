package com.tabila.backend.api;

import com.tabila.backend.api.dto.CategoryRequest;
import com.tabila.backend.api.dto.CategoryResponse;
import com.tabila.backend.api.dto.MenuItemRequest;
import com.tabila.backend.api.dto.MenuItemResponse;
import com.tabila.backend.api.dto.OrderResponse;
import com.tabila.backend.api.dto.PaymentsDashboardResponse;
import com.tabila.backend.api.dto.RestaurantConfigRequest;
import com.tabila.backend.api.dto.RestaurantConfigResponse;
import com.tabila.backend.api.dto.TableRequest;
import com.tabila.backend.api.dto.TableResponse;
import com.tabila.backend.api.dto.UpdateOrderStatusRequest;
import com.tabila.backend.service.AuthContextService;
import com.tabila.backend.service.RestaurantMvpService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RestaurantMvpService restaurantMvpService;
    private final AuthContextService authContextService;

    public AdminController(RestaurantMvpService restaurantMvpService, AuthContextService authContextService) {
        this.restaurantMvpService = restaurantMvpService;
        this.authContextService = authContextService;
    }

    @GetMapping("/tables")
    public List<TableResponse> listTables() {
        return restaurantMvpService.listTables(authContextService.currentRestaurantId());
    }

    @PostMapping("/tables")
    public TableResponse createTable(@Valid @RequestBody TableRequest request) {
        return restaurantMvpService.createTable(authContextService.currentRestaurantId(), request);
    }

    @PutMapping("/tables/{id}")
    public TableResponse updateTable(@PathVariable Long id, @Valid @RequestBody TableRequest request) {
        return restaurantMvpService.updateTable(authContextService.currentRestaurantId(), id, request);
    }

    @DeleteMapping("/tables/{id}")
    public void deleteTable(@PathVariable Long id) {
        restaurantMvpService.deleteTable(authContextService.currentRestaurantId(), id);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> listCategories() {
        return restaurantMvpService.listCategories(authContextService.currentRestaurantId());
    }

    @PostMapping("/categories")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return restaurantMvpService.createCategory(authContextService.currentRestaurantId(), request);
    }

    @PutMapping("/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return restaurantMvpService.updateCategory(authContextService.currentRestaurantId(), id, request);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        restaurantMvpService.deleteCategory(authContextService.currentRestaurantId(), id);
    }

    @GetMapping("/menu-items")
    public List<MenuItemResponse> listMenuItems() {
        return restaurantMvpService.listMenuItems(authContextService.currentRestaurantId());
    }

    @PostMapping("/menu-items")
    public MenuItemResponse createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        return restaurantMvpService.createMenuItem(authContextService.currentRestaurantId(), request);
    }

    @PutMapping("/menu-items/{id}")
    public MenuItemResponse updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemRequest request) {
        return restaurantMvpService.updateMenuItem(authContextService.currentRestaurantId(), id, request);
    }

    @DeleteMapping("/menu-items/{id}")
    public void deleteMenuItem(@PathVariable Long id) {
        restaurantMvpService.deleteMenuItem(authContextService.currentRestaurantId(), id);
    }

    @GetMapping("/orders")
    public List<OrderResponse> listOrders() {
        return restaurantMvpService.listOrders(authContextService.currentRestaurantId());
    }

    @PatchMapping("/orders/{id}/status")
    public OrderResponse updateOrderStatus(@PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return restaurantMvpService.updateOrderStatus(authContextService.currentRestaurantId(), id, request.status());
    }

    @GetMapping("/payments")
    public PaymentsDashboardResponse paymentsDashboard() {
        return restaurantMvpService.getPaymentsDashboard(authContextService.currentRestaurantId());
    }

    @GetMapping("/restaurant-config")
    public RestaurantConfigResponse getRestaurantConfig() {
        return restaurantMvpService.getRestaurantConfig(authContextService.currentRestaurantId());
    }

    @PutMapping("/restaurant-config")
    public RestaurantConfigResponse updateRestaurantConfig(@Valid @RequestBody RestaurantConfigRequest request) {
        return restaurantMvpService.updateRestaurantConfig(authContextService.currentRestaurantId(), request);
    }
}
