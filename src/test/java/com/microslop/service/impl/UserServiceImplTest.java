package com.microslop.service.impl;

import com.microslop.entity.User;
import com.microslop.repository.UserRepository;
import com.microslop.command.CommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CommandExecutor commandExecutor;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        // no-op: se usa Mockito para inicializar los mocks
    }

    @Test
    void should_save_user_when_registration_is_valid() {
        User newUser = new User("Jane Doe", "jane@example.com", "janedoe", "secret123", LocalDateTime.now().minusYears(20));
        when(userRepository.existsByUsernameIgnoreCase("janedoe")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret123");

        userService.registerUser(newUser);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("encoded-secret123");
        assertThat(savedUser.getUsername()).isEqualTo("janedoe");
        assertThat(savedUser.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void should_reject_duplicate_username() {
        User newUser = new User("John Doe", "john@example.com", "johndoe", "secret123", LocalDateTime.now().minusYears(25));
        when(userRepository.existsByUsernameIgnoreCase("johndoe")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(com.microslop.exception.BusinessValidationException.class)
                .hasMessage("Username is already in use. Choose another one.");
    }

    @Test
    void should_reject_duplicate_email() {
        User newUser = new User("John Doe", "john@example.com", "johndoe", "secret123", LocalDateTime.now().minusYears(25));
        when(userRepository.existsByUsernameIgnoreCase("johndoe")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(com.microslop.exception.BusinessValidationException.class)
                .hasMessage("An account with this email already exists.");
    }

    @Test
    void should_reject_null_password() {
        User newUser = new User("John Doe", "john@example.com", "johndoe", null, LocalDateTime.now().minusYears(25));
        when(userRepository.existsByUsernameIgnoreCase("johndoe")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("john@example.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(com.microslop.exception.BusinessValidationException.class)
                .hasMessage("Password cannot be null.");
    }

    @Test
    void should_reject_short_password() {
        User newUser = new User("John Doe", "john@example.com", "johndoe", "12345", LocalDateTime.now().minusYears(25));
        when(userRepository.existsByUsernameIgnoreCase("johndoe")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("john@example.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(com.microslop.exception.BusinessValidationException.class)
                .hasMessage("Password must be at least 6 characters long.");
    }

    @Test
    void should_reject_too_young_user() {
        User newUser = new User("Young User", "young@example.com", "younguser", "secret123", LocalDateTime.now().minusYears(12));
        when(userRepository.existsByUsernameIgnoreCase("younguser")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("young@example.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(com.microslop.exception.BusinessValidationException.class)
                .hasMessage("You must be at least 13 years old to register.");
    }

    @Test
    void should_reject_future_birth_date() {
        User newUser = new User("Future User", "future@example.com", "futureuser", "secret123", LocalDateTime.now().plusDays(1));
        when(userRepository.existsByUsernameIgnoreCase("futureuser")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("future@example.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(com.microslop.exception.BusinessValidationException.class)
                .hasMessage("Birth date cannot be in the future.");
    }

    @Test
    void should_find_user_by_username() {
        User user = new User("Test User", "test@example.com", "testuser", "hashedpassword", LocalDateTime.now().minusYears(20));
        when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(java.util.Optional.of(user));

        var result = userService.searchByUsernameIgnoreCase("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void should_return_empty_when_user_not_found() {
        when(userRepository.findByUsernameIgnoreCase("nonexistent")).thenReturn(java.util.Optional.empty());

        var result = userService.searchByUsernameIgnoreCase("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_user_by_id() {
        User user = new User("Test User", "test@example.com", "testuser", "hashedpassword", LocalDateTime.now().minusYears(20));
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        var result = userService.getUserById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }
}
