package com.tabila.backend.service;

import com.tabila.backend.api.dto.CreateJoinRequestRequest;
import com.tabila.backend.api.dto.JoinRequestResponse;
import com.tabila.backend.domain.JoinRequest;
import com.tabila.backend.domain.enums.JoinRequestStatus;
import com.tabila.backend.repository.JoinRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;

    public JoinRequestService(JoinRequestRepository joinRequestRepository) {
        this.joinRequestRepository = joinRequestRepository;
    }

    public JoinRequestResponse create(CreateJoinRequestRequest request) {
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setRestaurantName(request.restaurantName().trim());
        joinRequest.setContactName(request.contactName().trim());
        joinRequest.setContactEmail(request.contactEmail().trim().toLowerCase());
        joinRequest.setContactPhone(request.contactPhone().trim());
        joinRequest.setMessage(request.message() == null ? null : request.message().trim());
        joinRequest.setStatus(JoinRequestStatus.PENDING);
        joinRequest = joinRequestRepository.save(joinRequest);

        return new JoinRequestResponse(
                joinRequest.getId(),
                joinRequest.getRestaurantName(),
                joinRequest.getContactName(),
                joinRequest.getContactEmail(),
                joinRequest.getContactPhone(),
                joinRequest.getMessage(),
                joinRequest.getStatus(),
                joinRequest.getCreatedAt());
    }
}
