package com.example.accessingdatamysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
    }

    @Test
    void shouldSaveUser() {
        User saved = userRepository.save(user);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("John Doe");
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldFindUserById() {
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> found = userRepository.findById(999);

        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllUsers() {
        userRepository.save(user);

        User user2 = new User();
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");
        userRepository.save(user2);

        List<User> users = (List<User>) userRepository.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void shouldUpdateUser() {
        User saved = userRepository.save(user);

        saved.setName("John Updated");
        saved.setEmail("john.updated@example.com");
        User updated = userRepository.save(saved);

        assertThat(updated.getName()).isEqualTo("John Updated");
        assertThat(updated.getEmail()).isEqualTo("john.updated@example.com");
    }

    @Test
    void shouldDeleteUserById() {
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCountUsers() {
        userRepository.save(user);

        User user2 = new User();
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");
        userRepository.save(user2);

        long count = userRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldCheckIfUserExists() {
        User saved = userRepository.save(user);

        boolean exists = userRepository.existsById(saved.getId());
        boolean notExists = userRepository.existsById(999);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
