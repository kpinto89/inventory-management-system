package com.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    private final MockMvc mockMvc;
    private final DaoAuthenticationProvider authenticationProvider;
    private final AuthenticationManager authenticationManager;
    private final SecurityFilterChain securityFilterChain;

    @Autowired
    SecurityIntegrationTest(
            MockMvc mockMvc,
            DaoAuthenticationProvider authenticationProvider,
            AuthenticationManager authenticationManager,
            SecurityFilterChain securityFilterChain
    ) {
        this.mockMvc = mockMvc;
        this.authenticationProvider = authenticationProvider;
        this.authenticationManager = authenticationManager;
        this.securityFilterChain = securityFilterChain;
    }

    @Test
    void securityBeansAreCreated() {
        assertThat(authenticationProvider).isNotNull();
        assertThat(authenticationManager).isNotNull();
        assertThat(securityFilterChain).isNotNull();
    }

    @Test
    void unauthenticatedRequestsRedirectToLoginAndLoginPageLoadsMessages() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("errorMessage", "logoutMessage"));

        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "Invalid username or password. Please try again."));

        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("logoutMessage", "You have been logged out successfully."));
    }

    @Test
    void validAndInvalidLoginFlowsWork() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", "wrong-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void logoutAndH2ConsoleAccessAreAllowed() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"));

        mockMvc.perform(post("/h2-console/test"))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403))
                .andExpect(header().string("X-Frame-Options", "SAMEORIGIN"));
    }
}
