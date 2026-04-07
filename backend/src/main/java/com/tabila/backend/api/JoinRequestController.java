package com.tabila.backend.api;

import com.tabila.backend.api.dto.CreateJoinRequestRequest;
import com.tabila.backend.api.dto.JoinRequestResponse;
import com.tabila.backend.service.JoinRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    public JoinRequestController(JoinRequestService joinRequestService) {
        this.joinRequestService = joinRequestService;
    }

    @PostMapping("/api/join-requests")
    public JoinRequestResponse create(@Valid @RequestBody CreateJoinRequestRequest request) {
        return joinRequestService.create(request);
    }
}
