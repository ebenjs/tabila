package com.tabila.backend.repository;

import com.tabila.backend.domain.CustomerOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findAllByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
}
