package com.tabila.backend.repository;

import com.tabila.backend.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByRestaurantIdOrderByDisplayOrderAscIdAsc(Long restaurantId);
}
