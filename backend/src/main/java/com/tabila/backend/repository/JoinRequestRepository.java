package com.tabila.backend.repository;

import com.tabila.backend.domain.JoinRequest;
import com.tabila.backend.domain.enums.JoinRequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    List<JoinRequest> findAllByStatusOrderByCreatedAtDesc(JoinRequestStatus status);

    List<JoinRequest> findAllByOrderByCreatedAtDesc();
}
