package com.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest {

    private final AuthController authController = new AuthController();

    @Test
    void loginPageWithoutFlagsReturnsLoginViewOnly() {
        ExtendedModelMap model = new ExtendedModelMap();

        String view = authController.loginPage(null, null, model);

        assertThat(view).isEqualTo("auth/login");
        assertThat(model).doesNotContainKeys("errorMessage", "logoutMessage");
    }

    @Test
    void loginPageWithErrorAndLogoutAddsMessages() {
        ExtendedModelMap model = new ExtendedModelMap();

        String view = authController.loginPage("true", "true", model);

        assertThat(view).isEqualTo("auth/login");
        assertThat(model.get("errorMessage")).isEqualTo("Invalid username or password. Please try again.");
        assertThat(model.get("logoutMessage")).isEqualTo("You have been logged out successfully.");
    }
}

