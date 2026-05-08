package com.demo.service;

import com.demo.model.Category;
import com.demo.model.Product;
import com.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void delegatesBasicCrudOperations() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-1");

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findBySku("SKU-1")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productRepository.findByCategory(Category.ELECTRONICS)).thenReturn(List.of(product));

        assertThat(productService.getAllProducts()).containsExactly(product);
        assertThat(productService.getProductById(1L)).contains(product);
        assertThat(productService.getProductBySku("SKU-1")).contains(product);
        assertThat(productService.saveProduct(product)).isSameAs(product);
        assertThat(productService.getProductsByCategory(Category.ELECTRONICS)).containsExactly(product);

        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void searchProductsReturnsAllWhenQueryIsNullOrBlank() {
        Product product = new Product();
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertThat(productService.searchProducts(null)).containsExactly(product);
        assertThat(productService.searchProducts("   ")).containsExactly(product);
    }

    @Test
    void searchProductsUsesRepositoryForNonBlankQuery() {
        Product product = new Product();
        when(productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase("lap", "lap"))
                .thenReturn(List.of(product));

        assertThat(productService.searchProducts("lap")).containsExactly(product);
    }

    @Test
    void lowStockCountersAndInventoryValueAreCalculated() {
        Product product = new Product();
        when(productRepository.findLowStockProducts()).thenReturn(List.of(product));
        when(productRepository.countLowStockProducts()).thenReturn(2L);
        when(productRepository.count()).thenReturn(5L);
        when(productRepository.calculateTotalInventoryValue()).thenReturn(new BigDecimal("42.50"));

        assertThat(productService.getLowStockProducts()).containsExactly(product);
        assertThat(productService.countLowStockProducts()).isEqualTo(2L);
        assertThat(productService.countTotalProducts()).isEqualTo(5L);
        assertThat(productService.calculateTotalInventoryValue()).isEqualByComparingTo("42.50");
    }

    @Test
    void calculateTotalInventoryValueFallsBackToZeroWhenRepositoryReturnsNull() {
        when(productRepository.calculateTotalInventoryValue()).thenReturn(null);

        assertThat(productService.calculateTotalInventoryValue()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void isSkuUniqueHandlesMissingMatchingAndDifferentProducts() {
        Product product = new Product();
        product.setId(8L);

        when(productRepository.findBySku("missing")).thenReturn(Optional.empty());
        when(productRepository.findBySku("same")).thenReturn(Optional.of(product));
        when(productRepository.findBySku("taken")).thenReturn(Optional.of(product));

        assertThat(productService.isSkuUnique("missing", null)).isTrue();
        assertThat(productService.isSkuUnique("same", 8L)).isTrue();
        assertThat(productService.isSkuUnique("taken", null)).isFalse();
        assertThat(productService.isSkuUnique("taken", 99L)).isFalse();
    }
}

