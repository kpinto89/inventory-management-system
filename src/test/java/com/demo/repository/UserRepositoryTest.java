package com.demo.repository;

import com.demo.model.Role;
import com.demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User("admin", "encoded", "Administrator", Role.ROLE_ADMIN);
        userRepository.save(user);
    }

    @Test
    void findByUsernameAndExistsByUsernameWorkForPresentAndMissingUsers() {
        assertThat(userRepository.findByUsername("admin")).isPresent();
        assertThat(userRepository.findByUsername("missing")).isEmpty();
        assertThat(userRepository.existsByUsername("admin")).isTrue();
        assertThat(userRepository.existsByUsername("missing")).isFalse();
    }
}

