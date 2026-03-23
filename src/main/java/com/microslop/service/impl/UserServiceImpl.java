package com.microslop.service.impl;

import com.microslop.entity.User;
import com.microslop.repository.UserRepository;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl { //aqui me pone q deberia ponerle implements "implements UserService" (aunq es cierto q sino habria q hacer los otros metodos)

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User newUser) {
        if (userRepository.existsByUsernameIgnoreCase(newUser.getUsername())) {
            throw new IllegalArgumentException("Username is already in use. Choose another one.");
        }

        if (userRepository.existsByEmailIgnoreCase(newUser.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        if (newUser.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }

        if (newUser.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        if (LocalDate.now().minusYears(13).isBefore(newUser.getBirthDate().toLocalDate())) {
            throw new IllegalArgumentException("You must be at least 13 years old to register.");
        }

        if (LocalDate.now().isBefore(newUser.getBirthDate().toLocalDate())) {
            throw new IllegalArgumentException("Birth date cannot be in the future.");
        }

        String encryptedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encryptedPassword);

        userRepository.save(newUser);
    }

    public void login(String username, String password) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
    }
}

