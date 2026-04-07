package com.tabila.backend.repository;

import com.tabila.backend.domain.Payment;
import com.tabila.backend.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByOrderRestaurantIdOrderByCreatedAtDesc(Long restaurantId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.order.restaurant.id = :restaurantId and p.status = :status")
    BigDecimal sumAmountByRestaurantIdAndStatus(Long restaurantId, PaymentStatus status);
}
