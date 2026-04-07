package com.tabila.backend.repository;

import com.tabila.backend.domain.RestaurantTable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findAllByRestaurantIdOrderByIdAsc(Long restaurantId);
}
