package com.tabila.backend.config;

import com.tabila.backend.domain.AdminUser;
import com.tabila.backend.domain.Category;
import com.tabila.backend.domain.MenuItem;
import com.tabila.backend.domain.Restaurant;
import com.tabila.backend.domain.RestaurantTable;
import com.tabila.backend.domain.enums.AdminRole;
import com.tabila.backend.repository.AdminUserRepository;
import com.tabila.backend.repository.CategoryRepository;
import com.tabila.backend.repository.MenuItemRepository;
import com.tabila.backend.repository.RestaurantRepository;
import com.tabila.backend.repository.RestaurantTableRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository,
            CategoryRepository categoryRepository,
            MenuItemRepository menuItemRepository,
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminUserRepository.findByEmail("superadmin@tabila.local").isEmpty()) {
                AdminUser superAdmin = new AdminUser();
                superAdmin.setEmail("superadmin@tabila.local");
                superAdmin.setPasswordHash(passwordEncoder.encode("superadmin123"));
                superAdmin.setRole(AdminRole.SUPER_ADMIN);
                superAdmin.setRestaurant(null);
                adminUserRepository.save(superAdmin);
            }

            if (restaurantRepository.count() > 0) {
                return;
            }

            Restaurant restaurant = new Restaurant();
            restaurant.setName("Tabila Demo");
            restaurant.setCurrency("XAF");
            restaurant.setWelcomeMessage("Bienvenue au restaurant Tabila Demo");
            restaurant.setBackgroundColor("#ecfeff");
            restaurant.setAccentColor("#0f766e");
            restaurant = restaurantRepository.save(restaurant);

            RestaurantTable table1 = new RestaurantTable();
            table1.setName("Table 1");
            table1.setRestaurant(restaurant);
            table1.setQrCodeUrl("/menu?tableId=1");
            tableRepository.save(table1);

            RestaurantTable table2 = new RestaurantTable();
            table2.setName("Table 2");
            table2.setRestaurant(restaurant);
            table2.setQrCodeUrl("/menu?tableId=2");
            tableRepository.save(table2);

            Category plats = new Category();
            plats.setName("Plats");
            plats.setDisplayOrder(1);
            plats.setRestaurant(restaurant);
            plats = categoryRepository.save(plats);

            Category boissons = new Category();
            boissons.setName("Boissons");
            boissons.setDisplayOrder(2);
            boissons.setRestaurant(restaurant);
            boissons = categoryRepository.save(boissons);

            MenuItem menuItem1 = new MenuItem();
            menuItem1.setName("Poulet braise");
            menuItem1.setDescription("Poulet braise avec alloco");
            menuItem1.setPrice(new BigDecimal("4500"));
            menuItem1.setAvailable(true);
            menuItem1.setCategory(plats);
            menuItem1.setRestaurant(restaurant);
            menuItemRepository.save(menuItem1);

            MenuItem menuItem2 = new MenuItem();
            menuItem2.setName("Jus gingembre");
            menuItem2.setDescription("Jus maison frais");
            menuItem2.setPrice(new BigDecimal("1000"));
            menuItem2.setAvailable(true);
            menuItem2.setCategory(boissons);
            menuItem2.setRestaurant(restaurant);
            menuItemRepository.save(menuItem2);

            AdminUser admin = new AdminUser();
            admin.setEmail("admin@tabila.local");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(AdminRole.ADMIN);
            admin.setRestaurant(restaurant);
            adminUserRepository.save(admin);

        };
    }
}
