package com.demo.config;

import com.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void passwordEncoderEncodesPasswords() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        assertThat(encoder.matches("secret", encoder.encode("secret"))).isTrue();
    }

    @Test
    void authenticationProviderUsesUserServiceAndPasswordEncoder() {
        UserService userService = mock(UserService.class);

        DaoAuthenticationProvider provider = securityConfig.authenticationProvider(userService);

        assertThat(provider).isNotNull();
        assertThat(provider.supports(org.springframework.security.authentication.UsernamePasswordAuthenticationToken.class)).isTrue();
    }

    @Test
    void authenticationManagerDelegatesToConfiguration() throws Exception {
        AuthenticationConfiguration configuration = mock(AuthenticationConfiguration.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(configuration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager result = securityConfig.authenticationManager(configuration);

        assertThat(result).isSameAs(authenticationManager);
    }
}

