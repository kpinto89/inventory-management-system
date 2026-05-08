package com.demo.controller;

import com.demo.model.Category;
import com.demo.model.Product;
import com.demo.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController productController;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productService);
    }

    @Test
    void dashboardPopulatesModel() {
        ExtendedModelMap model = new ExtendedModelMap();
        Product recent = product(1L, "Recent", Category.ELECTRONICS, 5, 1, LocalDateTime.now());
        Product older = product(2L, "Older", Category.CLOTHING, 10, 1, LocalDateTime.now().minusDays(1));

        when(productService.countTotalProducts()).thenReturn(2L);
        when(productService.countLowStockProducts()).thenReturn(1L);
        when(productService.calculateTotalInventoryValue()).thenReturn(new BigDecimal("99.99"));
        when(productService.getLowStockProducts()).thenReturn(List.of(recent));
        when(productService.getAllProducts()).thenReturn(List.of(older, recent));

        String view = productController.dashboard(model);

        assertThat(view).isEqualTo("dashboard");
        assertThat(model.get("totalProducts")).isEqualTo(2L);
        assertThat(model.get("lowStockCount")).isEqualTo(1L);
        assertThat(model.get("totalInventoryValue")).isEqualTo(new BigDecimal("99.99"));
        assertThat((List<Product>) model.get("lowStockProducts")).containsExactly(recent);
        assertThat((List<Product>) model.get("recentProducts")).containsExactly(recent, older);
    }

    @Test
    void listProductsUsesAllProductsWhenNoSearchOrCategory() {
        ExtendedModelMap model = new ExtendedModelMap();
        Product product = product(1L, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        when(productService.getAllProducts()).thenReturn(List.of(product));

        String view = productController.listProducts(null, null, model);

        assertThat(view).isEqualTo("products/list");
        assertThat((List<Product>) model.get("products")).containsExactly(product);
        assertThat(model.get("search")).isNull();
        assertThat((Category[]) model.get("categories")).contains(Category.ELECTRONICS);
    }

    @Test
    void listProductsTreatsBlankSearchAndCategoryAsNotProvided() {
        ExtendedModelMap model = new ExtendedModelMap();
        Product product = product(1L, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        when(productService.getAllProducts()).thenReturn(List.of(product));

        String view = productController.listProducts("   ", "   ", model);

        assertThat(view).isEqualTo("products/list");
        assertThat((List<Product>) model.get("products")).containsExactly(product);
        assertThat(model.get("search")).isEqualTo("   ");
        assertThat(model.containsAttribute("selectedCategory")).isFalse();
        verify(productService, never()).searchProducts(any());
    }

    @Test
    void listProductsUsesSearchAndCategoryFilterWhenProvided() {
        ExtendedModelMap model = new ExtendedModelMap();
        Product matching = product(1L, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        Product other = product(2L, "Shirt", Category.CLOTHING, 8, 2, LocalDateTime.now());
        when(productService.searchProducts("lap")).thenReturn(List.of(matching, other));

        String view = productController.listProducts("lap", "ELECTRONICS", model);

        assertThat(view).isEqualTo("products/list");
        assertThat((List<Product>) model.get("products")).containsExactly(matching);
        assertThat(model.get("selectedCategory")).isEqualTo("ELECTRONICS");
        assertThat(model.get("search")).isEqualTo("lap");
    }

    @Test
    void listProductsIgnoresInvalidCategory() {
        ExtendedModelMap model = new ExtendedModelMap();
        Product product = product(1L, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        when(productService.getAllProducts()).thenReturn(List.of(product));

        String view = productController.listProducts(null, "NOT_A_CATEGORY", model);

        assertThat(view).isEqualTo("products/list");
        assertThat((List<Product>) model.get("products")).containsExactly(product);
        assertThat(model.containsAttribute("selectedCategory")).isFalse();
    }

    @Test
    void showAddFormPopulatesDefaults() {
        ExtendedModelMap model = new ExtendedModelMap();

        String view = productController.showAddForm(model);

        assertThat(view).isEqualTo("products/form");
        assertThat(model.get("product")).isInstanceOf(Product.class);
        assertThat(model.get("pageTitle")).isEqualTo("Add New Product");
        assertThat((Category[]) model.get("categories")).contains(Category.OTHER);
    }

    @Test
    void addProductReturnsFormWhenSkuAlreadyExists() {
        Product product = product(null, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
        ExtendedModelMap model = new ExtendedModelMap();

        when(productService.isSkuUnique("SKU-1", null)).thenReturn(false);

        String view = productController.addProduct(product, bindingResult, model, new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("products/form");
        assertThat(bindingResult.hasFieldErrors("sku")).isTrue();
        assertThat(model.get("pageTitle")).isEqualTo("Add New Product");
        verify(productService, never()).saveProduct(any());
    }

    @Test
    void addProductSavesWhenValid() {
        Product product = product(null, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        when(productService.isSkuUnique("SKU-1", null)).thenReturn(true);

        String view = productController.addProduct(product, bindingResult, new ExtendedModelMap(), redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        assertThat((Map<String, Object>) redirectAttributes.getFlashAttributes())
                .containsEntry("successMessage", "Product 'Laptop' added successfully!");
        verify(productService).saveProduct(product);
    }

    @Test
    void showEditFormRedirectsWhenProductMissing() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        when(productService.getProductById(99L)).thenReturn(Optional.empty());

        String view = productController.showEditForm(99L, new ExtendedModelMap(), redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        assertThat((Map<String, Object>) redirectAttributes.getFlashAttributes())
                .containsEntry("errorMessage", "Product not found!");
    }

    @Test
    void showEditFormLoadsProductWhenPresent() {
        Product product = product(1L, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        ExtendedModelMap model = new ExtendedModelMap();
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        String view = productController.showEditForm(1L, model, new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("products/form");
        assertThat(model.get("product")).isSameAs(product);
        assertThat(model.get("pageTitle")).isEqualTo("Edit Product");
    }

    @Test
    void updateProductReturnsFormWhenSkuAlreadyExists() {
        Product product = product(1L, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
        ExtendedModelMap model = new ExtendedModelMap();

        when(productService.isSkuUnique("SKU-1", 1L)).thenReturn(false);

        String view = productController.updateProduct(1L, product, bindingResult, model, new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("products/form");
        assertThat(bindingResult.hasFieldErrors("sku")).isTrue();
        assertThat(model.get("pageTitle")).isEqualTo("Edit Product");
        verify(productService, never()).saveProduct(any());
    }

    @Test
    void updateProductSavesWhenValid() {
        Product product = product(null, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        when(productService.isSkuUnique("SKU-1", 1L)).thenReturn(true);

        String view = productController.updateProduct(1L, product, bindingResult, new ExtendedModelMap(), redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        assertThat(product.getId()).isEqualTo(1L);
        assertThat((Map<String, Object>) redirectAttributes.getFlashAttributes())
                .containsEntry("successMessage", "Product 'Laptop' updated successfully!");
        verify(productService).saveProduct(product);
    }

    @Test
    void deleteProductHandlesExistingAndMissingProducts() {
        Product product = product(1L, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        RedirectAttributesModelMap successAttributes = new RedirectAttributesModelMap();
        RedirectAttributesModelMap errorAttributes = new RedirectAttributesModelMap();

        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        when(productService.getProductById(2L)).thenReturn(Optional.empty());

        String successView = productController.deleteProduct(1L, successAttributes);
        String errorView = productController.deleteProduct(2L, errorAttributes);

        assertThat(successView).isEqualTo("redirect:/products");
        assertThat(errorView).isEqualTo("redirect:/products");
        assertThat((Map<String, Object>) successAttributes.getFlashAttributes())
                .containsEntry("successMessage", "Product 'Laptop' deleted successfully!");
        assertThat((Map<String, Object>) errorAttributes.getFlashAttributes())
                .containsEntry("errorMessage", "Product not found!");
        verify(productService).deleteProduct(1L);
    }

    @Test
    void viewProductHandlesExistingAndMissingProducts() {
        Product product = product(1L, "Laptop", Category.ELECTRONICS, 10, 5, LocalDateTime.now());
        ExtendedModelMap model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        when(productService.getProductById(2L)).thenReturn(Optional.empty());

        String detailView = productController.viewProduct(1L, model, new RedirectAttributesModelMap());
        String missingView = productController.viewProduct(2L, new ExtendedModelMap(), redirectAttributes);

        assertThat(detailView).isEqualTo("products/detail");
        assertThat(model.get("product")).isSameAs(product);
        assertThat(missingView).isEqualTo("redirect:/products");
        assertThat((Map<String, Object>) redirectAttributes.getFlashAttributes())
                .containsEntry("errorMessage", "Product not found!");
    }

    @Test
    void lowStockProductsLoadsView() {
        ExtendedModelMap model = new ExtendedModelMap();
        Product product = product(1L, "Laptop", Category.ELECTRONICS, 1, 5, LocalDateTime.now());
        when(productService.getLowStockProducts()).thenReturn(List.of(product));

        String view = productController.lowStockProducts(model);

        assertThat(view).isEqualTo("products/low-stock");
        assertThat((List<Product>) model.get("products")).containsExactly(product);
    }

    private Product product(Long id, String name, Category category, int quantity, int threshold, LocalDateTime createdAt) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(name + " description");
        product.setSku("SKU-1");
        product.setPrice(new BigDecimal("9.99"));
        product.setQuantity(quantity);
        product.setLowStockThreshold(threshold);
        product.setCategory(category);
        product.setCreatedAt(createdAt);
        product.setUpdatedAt(createdAt);
        return product;
    }
}

