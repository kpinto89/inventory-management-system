package com.demo.service;

import com.demo.model.Role;
import com.demo.model.User;
import com.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void loadUserByUsernameBuildsSpringSecurityUser() {
        User user = new User("admin", "encoded", "Administrator", Role.ROLE_ADMIN);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername("admin");

        assertThat(details.getUsername()).isEqualTo("admin");
        assertThat(details.getPassword()).isEqualTo("encoded");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: missing");
    }

    @Test
    void registerUserEncodesPasswordAndSavesUser() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser("newuser", "plain", "New User", Role.ROLE_USER);

        assertThat(saved.getUsername()).isEqualTo("newuser");
        assertThat(saved.getPassword()).isEqualTo("encoded");
        assertThat(saved.getFullName()).isEqualTo("New User");
        assertThat(saved.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    void registerUserRejectsDuplicateUsername() {
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser("taken", "plain", "Taken User", Role.ROLE_USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists: taken");
    }

    @Test
    void delegatesExistsAndFindAll() {
        User user = new User("user", "encoded", "Regular User", Role.ROLE_USER);
        when(userRepository.existsByUsername("user")).thenReturn(true);
        when(userRepository.findAll()).thenReturn(List.of(user));

        assertThat(userService.existsByUsername("user")).isTrue();
        assertThat(userService.getAllUsers()).containsExactly(user);
    }
}

