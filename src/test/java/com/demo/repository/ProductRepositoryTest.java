package com.demo.repository;

import com.demo.model.Category;
import com.demo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product laptop;
    private Product shirt;
    private Product mouse;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        laptop = saveProduct("Laptop", "ELEC-1", Category.ELECTRONICS, "100.00", 2, 3);
        shirt = saveProduct("Shirt", "CLTH-1", Category.CLOTHING, "20.00", 10, 2);
        mouse = saveProduct("Wireless Mouse", "ELEC-2", Category.ELECTRONICS, "30.00", 1, 2);
    }

    @Test
    void repositoryQueriesReturnExpectedData() {
        assertThat(productRepository.findByCategory(Category.ELECTRONICS))
                .extracting(Product::getSku)
                .containsExactlyInAnyOrder("ELEC-1", "ELEC-2");

        assertThat(productRepository.findByNameContainingIgnoreCase("lap"))
                .extracting(Product::getSku)
                .containsExactly("ELEC-1");

        assertThat(productRepository.findBySku("CLTH-1")).contains(shirt);
        assertThat(productRepository.findBySku("missing")).isEmpty();

        assertThat(productRepository.findLowStockProducts())
                .extracting(Product::getSku)
                .containsExactlyInAnyOrder("ELEC-1", "ELEC-2");

        assertThat(productRepository.countLowStockProducts()).isEqualTo(2L);
        assertThat(productRepository.calculateTotalInventoryValue()).isEqualByComparingTo("430.00");

        assertThat(productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase("wireless", "wireless"))
                .extracting(Product::getSku)
                .containsExactly("ELEC-2");

        assertThat(productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase("CLTH-1", "CLTH-1"))
                .extracting(Product::getSku)
                .containsExactly("CLTH-1");
    }

    private Product saveProduct(String name, String sku, Category category, String price, int quantity, int threshold) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(name + " description");
        product.setSku(sku);
        product.setPrice(new BigDecimal(price));
        product.setQuantity(quantity);
        product.setLowStockThreshold(threshold);
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
}

