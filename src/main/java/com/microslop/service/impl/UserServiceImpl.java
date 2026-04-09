package com.microslop.service.impl;

import com.microslop.entity.User;
import com.microslop.repository.UserRepository;
import com.microslop.service.UserService;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

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

    @Override
    public Optional<User> searchByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //Estos metodos no hacen nada en mi parte, solo estan aqui para que compile
    @Override
    public Optional<User> login(String username, String password) {
        return Optional.empty();
    }

    @Override
    public User updateProfile(Long id, String username, String email) {
        return null;
    }

    @Override
    public Optional<User> searchById(Long id) {
        return Optional.empty();
    }

    

}
