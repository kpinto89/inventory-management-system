package com.demo.service;

import com.demo.model.Category;
import com.demo.model.Product;
import com.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(query, query);
    }

    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    public long countLowStockProducts() {
        return productRepository.countLowStockProducts();
    }

    public long countTotalProducts() {
        return productRepository.count();
    }

    public BigDecimal calculateTotalInventoryValue() {
        BigDecimal value = productRepository.calculateTotalInventoryValue();
        return value != null ? value : BigDecimal.ZERO;
    }

    public boolean isSkuUnique(String sku, Long excludeId) {
        Optional<Product> existing = productRepository.findBySku(sku);
        if (existing.isEmpty()) return true;
        return excludeId != null && existing.get().getId().equals(excludeId);
    }
}

