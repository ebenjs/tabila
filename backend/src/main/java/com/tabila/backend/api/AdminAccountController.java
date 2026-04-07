package com.tabila.backend.api;

import com.tabila.backend.api.dto.ChangePasswordRequest;
import com.tabila.backend.service.AdminAccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/account")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    public AdminAccountController(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    @PatchMapping("/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        adminAccountService.changePassword(request);
    }
}
