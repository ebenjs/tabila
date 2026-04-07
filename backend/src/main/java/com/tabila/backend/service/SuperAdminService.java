package com.tabila.backend.service;

import com.tabila.backend.api.dto.CreateRestaurantAdminRequest;
import com.tabila.backend.api.dto.CreateRestaurantRequest;
import com.tabila.backend.api.dto.CreatedRestaurantAdminResponse;
import com.tabila.backend.api.dto.JoinRequestResponse;
import com.tabila.backend.api.dto.RestaurantSummaryResponse;
import com.tabila.backend.domain.AdminUser;
import com.tabila.backend.domain.JoinRequest;
import com.tabila.backend.domain.Restaurant;
import com.tabila.backend.domain.enums.AdminRole;
import com.tabila.backend.domain.enums.JoinRequestStatus;
import com.tabila.backend.repository.AdminUserRepository;
import com.tabila.backend.repository.JoinRequestRepository;
import com.tabila.backend.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class SuperAdminService {

    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#$%!";

    private final JoinRequestRepository joinRequestRepository;
    private final AdminUserRepository adminUserRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminCredentialEmailService adminCredentialEmailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public SuperAdminService(
            JoinRequestRepository joinRequestRepository,
            AdminUserRepository adminUserRepository,
            RestaurantRepository restaurantRepository,
            PasswordEncoder passwordEncoder,
            AdminCredentialEmailService adminCredentialEmailService) {
        this.joinRequestRepository = joinRequestRepository;
        this.adminUserRepository = adminUserRepository;
        this.restaurantRepository = restaurantRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminCredentialEmailService = adminCredentialEmailService;
    }

    public List<JoinRequestResponse> listJoinRequests() {
        return joinRequestRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toJoinRequestResponse)
                .toList();
    }

    public List<RestaurantSummaryResponse> listRestaurants() {
        return restaurantRepository
                .findAll()
                .stream()
                .map(r -> new RestaurantSummaryResponse(r.getId(), r.getName()))
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .toList();
    }

    public CreatedRestaurantAdminResponse createRestaurantAdmin(CreateRestaurantAdminRequest request) {
        String email = request.email().trim().toLowerCase();
        if (adminUserRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "Un utilisateur avec cet email existe deja");
        }

        Long restaurantId = Objects.requireNonNull(request.restaurantId(), "restaurantId est requis");

        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Restaurant introuvable"));

        String generatedPassword = generatePassword(12);

        AdminUser user = new AdminUser();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(generatedPassword));
        user.setRole(AdminRole.ADMIN);
        user.setRestaurant(restaurant);
        user = adminUserRepository.save(user);
        adminCredentialEmailService.sendRestaurantAdminCredentials(email, restaurant, generatedPassword);

        return new CreatedRestaurantAdminResponse(user.getId(), user.getEmail(), restaurant.getId(), true);
    }

    public RestaurantSummaryResponse createRestaurant(CreateRestaurantRequest request) {
        String name = request.name().trim();
        boolean exists = restaurantRepository.findAll().stream()
                .anyMatch(r -> r.getName() != null && r.getName().equalsIgnoreCase(name));
        if (exists) {
            throw new ResponseStatusException(CONFLICT, "Un restaurant avec ce nom existe deja");
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        Restaurant saved = restaurantRepository.save(restaurant);
        return new RestaurantSummaryResponse(saved.getId(), saved.getName());
    }

    public void markJoinRequestAsApproved(Long joinRequestId) {
        Long safeJoinRequestId = Objects.requireNonNull(joinRequestId, "joinRequestId est requis");
        JoinRequest request = joinRequestRepository
                .findById(safeJoinRequestId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Demande introuvable"));
        request.setStatus(JoinRequestStatus.APPROVED);
        joinRequestRepository.save(request);
    }

    private String generatePassword(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = secureRandom.nextInt(PASSWORD_CHARS.length());
            result.append(PASSWORD_CHARS.charAt(idx));
        }
        return result.toString();
    }

    private JoinRequestResponse toJoinRequestResponse(JoinRequest request) {
        return new JoinRequestResponse(
                request.getId(),
                request.getRestaurantName(),
                request.getContactName(),
                request.getContactEmail(),
                request.getContactPhone(),
                request.getMessage(),
                request.getStatus(),
                request.getCreatedAt());
    }
}
