package com.tabila.backend.repository;

import com.tabila.backend.domain.MenuItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByRestaurantIdOrderByIdAsc(Long restaurantId);

    List<MenuItem> findAllByRestaurantIdAndAvailableTrueOrderByIdAsc(Long restaurantId);
}
