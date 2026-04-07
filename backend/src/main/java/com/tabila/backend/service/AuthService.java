package com.tabila.backend.service;

import com.tabila.backend.api.dto.LoginRequest;
import com.tabila.backend.api.dto.LoginResponse;
import com.tabila.backend.domain.AdminUser;
import com.tabila.backend.repository.AdminUserRepository;
import com.tabila.backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdminUserRepository adminUserRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AdminUserRepository adminUserRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.adminUserRepository = adminUserRepository;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        AdminUser user = adminUserRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Utilisateur introuvable"));
        String token = jwtService.generateToken(request.email());
        return new LoginResponse(token, "Bearer", jwtService.getExpirationSeconds(), user.getRole().name());
    }
}
