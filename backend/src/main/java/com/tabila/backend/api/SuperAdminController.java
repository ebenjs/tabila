package com.tabila.backend.api;

import com.tabila.backend.api.dto.CreateRestaurantAdminRequest;
import com.tabila.backend.api.dto.CreateRestaurantRequest;
import com.tabila.backend.api.dto.CreatedRestaurantAdminResponse;
import com.tabila.backend.api.dto.JoinRequestResponse;
import com.tabila.backend.api.dto.RestaurantSummaryResponse;
import com.tabila.backend.service.SuperAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    @GetMapping("/join-requests")
    public List<JoinRequestResponse> listJoinRequests() {
        return superAdminService.listJoinRequests();
    }

    @GetMapping("/restaurants")
    public List<RestaurantSummaryResponse> listRestaurants() {
        return superAdminService.listRestaurants();
    }

    @PostMapping("/users")
    public CreatedRestaurantAdminResponse createRestaurantAdmin(
            @Valid @RequestBody CreateRestaurantAdminRequest request) {
        return superAdminService.createRestaurantAdmin(request);
    }

    @PostMapping("/restaurants")
    public RestaurantSummaryResponse createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        return superAdminService.createRestaurant(request);
    }

    @PatchMapping("/join-requests/{id}/approve")
    public void approveJoinRequest(@PathVariable Long id) {
        superAdminService.markJoinRequestAsApproved(id);
    }
}
