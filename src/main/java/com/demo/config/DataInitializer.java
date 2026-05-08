package com.demo.config;

import com.demo.model.Category;
import com.demo.model.Product;
import com.demo.model.Role;
import com.demo.repository.ProductRepository;
import com.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(ProductRepository repository, UserService userService) {
        return args -> {
            // Seed default users
            if (!userService.existsByUsername("admin")) {
                userService.registerUser("admin", "admin123", "Administrator", Role.ROLE_ADMIN);
            }
            if (!userService.existsByUsername("user")) {
                userService.registerUser("user", "user123", "Regular User", Role.ROLE_USER);
            }

            // Seed sample products
            if (repository.count() == 0) {
                Product p1 = new Product();
                p1.setName("Laptop Pro 15");
                p1.setSku("ELEC-001");
                p1.setDescription("High-performance laptop with 16GB RAM and 512GB SSD");
                p1.setPrice(new BigDecimal("1299.99"));
                p1.setQuantity(25);
                p1.setLowStockThreshold(5);
                p1.setCategory(Category.ELECTRONICS);
                repository.save(p1);

                Product p2 = new Product();
                p2.setName("Wireless Mouse");
                p2.setSku("ELEC-002");
                p2.setDescription("Ergonomic wireless mouse with 3-year battery life");
                p2.setPrice(new BigDecimal("39.99"));
                p2.setQuantity(8);
                p2.setLowStockThreshold(10);
                p2.setCategory(Category.ELECTRONICS);
                repository.save(p2);

                Product p3 = new Product();
                p3.setName("Office Chair Deluxe");
                p3.setSku("FURN-001");
                p3.setDescription("Ergonomic office chair with lumbar support");
                p3.setPrice(new BigDecimal("349.99"));
                p3.setQuantity(15);
                p3.setLowStockThreshold(5);
                p3.setCategory(Category.FURNITURE);
                repository.save(p3);

                Product p4 = new Product();
                p4.setName("Ballpoint Pen Pack");
                p4.setSku("OFFC-001");
                p4.setDescription("Pack of 12 blue ballpoint pens");
                p4.setPrice(new BigDecimal("5.99"));
                p4.setQuantity(3);
                p4.setLowStockThreshold(20);
                p4.setCategory(Category.OFFICE_SUPPLIES);
                repository.save(p4);

                Product p5 = new Product();
                p5.setName("Running Shoes");
                p5.setSku("SPRT-001");
                p5.setDescription("Lightweight running shoes for all terrains");
                p5.setPrice(new BigDecimal("89.99"));
                p5.setQuantity(40);
                p5.setLowStockThreshold(10);
                p5.setCategory(Category.SPORTS);
                repository.save(p5);

                Product p6 = new Product();
                p6.setName("Vitamin C Supplement");
                p6.setSku("HLTH-001");
                p6.setDescription("1000mg Vitamin C tablets, 90 count");
                p6.setPrice(new BigDecimal("14.99"));
                p6.setQuantity(6);
                p6.setLowStockThreshold(15);
                p6.setCategory(Category.HEALTH_AND_BEAUTY);
                repository.save(p6);

                Product p7 = new Product();
                p7.setName("USB-C Hub 7-in-1");
                p7.setSku("ELEC-003");
                p7.setDescription("7-port USB-C hub with HDMI, USB 3.0, SD card reader");
                p7.setPrice(new BigDecimal("49.99"));
                p7.setQuantity(30);
                p7.setLowStockThreshold(5);
                p7.setCategory(Category.ELECTRONICS);
                repository.save(p7);

                Product p8 = new Product();
                p8.setName("Cotton T-Shirt");
                p8.setSku("CLTH-001");
                p8.setDescription("100% organic cotton t-shirt, unisex");
                p8.setPrice(new BigDecimal("19.99"));
                p8.setQuantity(60);
                p8.setLowStockThreshold(20);
                p8.setCategory(Category.CLOTHING);
                repository.save(p8);
            }
        };
    }
}
