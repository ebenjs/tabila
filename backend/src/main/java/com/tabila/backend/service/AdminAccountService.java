package com.tabila.backend.service;

import com.tabila.backend.api.dto.ChangePasswordRequest;
import com.tabila.backend.domain.AdminUser;
import com.tabila.backend.repository.AdminUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Transactional
public class AdminAccountService {

    private final AuthContextService authContextService;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAccountService(
            AuthContextService authContextService,
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder) {
        this.authContextService = authContextService;
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void changePassword(ChangePasswordRequest request) {
        AdminUser admin = authContextService.currentAdmin();
        if (!passwordEncoder.matches(request.currentPassword(), admin.getPasswordHash())) {
            throw new ResponseStatusException(BAD_REQUEST, "Mot de passe actuel invalide");
        }
        admin.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        adminUserRepository.save(admin);
    }
}
