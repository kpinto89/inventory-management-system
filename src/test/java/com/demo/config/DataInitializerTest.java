package com.demo.config;

import com.demo.model.Role;
import com.demo.repository.ProductRepository;
import com.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    private final DataInitializer dataInitializer = new DataInitializer();

    @Test
    void seedsUsersAndProductsWhenMissing() throws Exception {
        ProductRepository productRepository = mock(ProductRepository.class);
        UserService userService = mock(UserService.class);
        CommandLineRunner runner = dataInitializer.loadData(productRepository, userService);

        when(userService.existsByUsername("admin")).thenReturn(false);
        when(userService.existsByUsername("user")).thenReturn(false);
        when(productRepository.count()).thenReturn(0L);

        runner.run();

        verify(userService).registerUser("admin", "admin123", "Administrator", Role.ROLE_ADMIN);
        verify(userService).registerUser("user", "user123", "Regular User", Role.ROLE_USER);
        verify(productRepository, times(8)).save(any());
    }

    @Test
    void skipsSeedingWhenDataAlreadyExists() throws Exception {
        ProductRepository productRepository = mock(ProductRepository.class);
        UserService userService = mock(UserService.class);
        CommandLineRunner runner = dataInitializer.loadData(productRepository, userService);

        when(userService.existsByUsername("admin")).thenReturn(true);
        when(userService.existsByUsername("user")).thenReturn(true);
        when(productRepository.count()).thenReturn(5L);

        runner.run();

        verify(userService, never()).registerUser(any(), any(), any(), any());
        verify(productRepository, never()).save(any());
    }
}

