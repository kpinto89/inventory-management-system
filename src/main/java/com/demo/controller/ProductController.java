package com.demo.controller;

import com.demo.model.Category;
import com.demo.model.Product;
import com.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Dashboard
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.countTotalProducts());
        model.addAttribute("lowStockCount", productService.countLowStockProducts());
        model.addAttribute("totalInventoryValue", productService.calculateTotalInventoryValue());
        model.addAttribute("lowStockProducts", productService.getLowStockProducts());
        model.addAttribute("recentProducts", productService.getAllProducts().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5).toList());
        return "dashboard";
    }

    // List all products
    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "category", required = false) String category,
                               Model model) {
        var products = (search != null && !search.isBlank())
                ? productService.searchProducts(search)
                : productService.getAllProducts();

        if (category != null && !category.isBlank()) {
            try {
                Category cat = Category.valueOf(category);
                products = products.stream().filter(p -> p.getCategory() == cat).toList();
                model.addAttribute("selectedCategory", category);
            } catch (IllegalArgumentException ignored) {}
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", Category.values());
        model.addAttribute("search", search);
        return "products/list";
    }

    // Show add product form
    @GetMapping("/products/new")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Category.values());
        model.addAttribute("pageTitle", "Add New Product");
        return "products/form";
    }

    // Handle add product
    @PostMapping("/products/new")
    public String addProduct(@Valid @ModelAttribute Product product,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (!productService.isSkuUnique(product.getSku(), null)) {
            result.rejectValue("sku", "sku.duplicate", "SKU already exists");
        }
        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            model.addAttribute("pageTitle", "Add New Product");
            return "products/form";
        }
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Product '" + product.getName() + "' added successfully!");
        return "redirect:/products";
    }

    // Show edit product form
    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
            return "redirect:/products";
        }
        model.addAttribute("product", product.get());
        model.addAttribute("categories", Category.values());
        model.addAttribute("pageTitle", "Edit Product");
        return "products/form";
    }

    // Handle edit product
    @PostMapping("/products/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute Product product,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (!productService.isSkuUnique(product.getSku(), id)) {
            result.rejectValue("sku", "sku.duplicate", "SKU already exists");
        }
        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            model.addAttribute("pageTitle", "Edit Product");
            return "products/form";
        }
        product.setId(id);
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Product '" + product.getName() + "' updated successfully!");
        return "redirect:/products";
    }

    // Delete product
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product '" + product.get().getName() + "' deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
        }
        return "redirect:/products";
    }

    // View product details
    @GetMapping("/products/{id}")
    public String viewProduct(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
            return "redirect:/products";
        }
        model.addAttribute("product", product.get());
        return "products/detail";
    }

    // Low stock page
    @GetMapping("/products/low-stock")
    public String lowStockProducts(Model model) {
        model.addAttribute("products", productService.getLowStockProducts());
        return "products/low-stock";
    }
}

