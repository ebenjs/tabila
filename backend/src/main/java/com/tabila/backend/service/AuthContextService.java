package com.tabila.backend.service;

import com.tabila.backend.domain.AdminUser;
import com.tabila.backend.domain.enums.AdminRole;
import com.tabila.backend.repository.AdminUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthContextService {

    private final AdminUserRepository adminUserRepository;

    public AuthContextService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    public AdminUser currentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Non authentifie");
        }
        return adminUserRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Admin introuvable"));
    }

    public Long currentRestaurantId() {
        AdminUser admin = currentAdmin();
        if (admin.getRole() != AdminRole.ADMIN) {
            throw new ResponseStatusException(FORBIDDEN, "Acces reserve aux admins restaurant");
        }
        if (admin.getRestaurant() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Aucun restaurant associe");
        }
        return admin.getRestaurant().getId();
    }

    public void requireSuperAdmin() {
        if (currentAdmin().getRole() != AdminRole.SUPER_ADMIN) {
            throw new ResponseStatusException(FORBIDDEN, "Acces reserve aux super admins");
        }
    }
}
