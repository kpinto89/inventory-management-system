package com.demo.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ModelCoverageTest {

    @Test
    void productGettersSettersAndLifecycleMethodsWork() {
        Product product = new Product();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);

        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Powerful laptop");
        product.setSku("SKU-1");
        product.setPrice(new BigDecimal("10.50"));
        product.setQuantity(5);
        product.setLowStockThreshold(7);
        product.setCategory(Category.ELECTRONICS);
        product.setCreatedAt(createdAt);
        product.setUpdatedAt(updatedAt);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Laptop");
        assertThat(product.getDescription()).isEqualTo("Powerful laptop");
        assertThat(product.getSku()).isEqualTo("SKU-1");
        assertThat(product.getPrice()).isEqualByComparingTo("10.50");
        assertThat(product.getQuantity()).isEqualTo(5);
        assertThat(product.getLowStockThreshold()).isEqualTo(7);
        assertThat(product.getCategory()).isEqualTo(Category.ELECTRONICS);
        assertThat(product.getCreatedAt()).isEqualTo(createdAt);
        assertThat(product.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(product.isLowStock()).isTrue();

        product.setQuantity(8);
        assertThat(product.isLowStock()).isFalse();

        product.setQuantity(null);
        assertThat(product.isLowStock()).isFalse();

        product.setQuantity(1);
        product.setLowStockThreshold(null);
        assertThat(product.isLowStock()).isFalse();

        product.onCreate();
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();

        LocalDateTime createdAfterPersist = product.getCreatedAt();
        product.setUpdatedAt(createdAfterPersist.minusMinutes(1));
        product.onUpdate();
        assertThat(product.getUpdatedAt()).isAfterOrEqualTo(createdAfterPersist);
    }

    @Test
    void userConstructorsDefaultsAndMutatorsWork() {
        User defaultUser = new User();
        assertThat(defaultUser.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(defaultUser.isEnabled()).isTrue();

        User user = new User("admin", "secret", "Admin User", Role.ROLE_ADMIN);
        user.setId(10L);
        user.setUsername("updatedUser");
        user.setPassword("updatedPassword");
        user.setFullName("Updated Name");
        user.setRole(Role.ROLE_USER);
        user.setEnabled(false);

        assertThat(user.getId()).isEqualTo(10L);
        assertThat(user.getUsername()).isEqualTo("updatedUser");
        assertThat(user.getPassword()).isEqualTo("updatedPassword");
        assertThat(user.getFullName()).isEqualTo("Updated Name");
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    void enumsExposeAllExpectedValuesAndValueOfWorks() {
        assertThat(Category.valueOf("ELECTRONICS")).isEqualTo(Category.ELECTRONICS);
        assertThat(Role.valueOf("ROLE_ADMIN")).isEqualTo(Role.ROLE_ADMIN);

        assertThat(Arrays.asList(Category.values())).containsExactly(
                Category.ELECTRONICS,
                Category.CLOTHING,
                Category.FOOD_AND_BEVERAGES,
                Category.FURNITURE,
                Category.OFFICE_SUPPLIES,
                Category.HEALTH_AND_BEAUTY,
                Category.SPORTS,
                Category.TOYS,
                Category.AUTOMOTIVE,
                Category.OTHER
        );
        assertThat(Arrays.asList(Role.values())).containsExactly(Role.ROLE_ADMIN, Role.ROLE_USER);
    }
}

