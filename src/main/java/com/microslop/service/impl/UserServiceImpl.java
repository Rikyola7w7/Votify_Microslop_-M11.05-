package com.microslop.service.impl;

import com.microslop.entity.User;
import com.microslop.repository.UserRepository;
import com.microslop.service.UserService;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

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

        if (LocalDate.now().isBefore(newUser.getBirthDate().toLocalDate())) {
            throw new IllegalArgumentException("Birth date cannot be in the future.");
        }

        if (LocalDate.now().minusYears(13).isBefore(newUser.getBirthDate().toLocalDate())) {
            throw new IllegalArgumentException("You must be at least 13 years old to register.");
        }

        String encryptedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encryptedPassword);
        userRepository.save(newUser);
    }

    @Override
    public Optional<User> searchByUsernameIgnoreCase(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public void login(String username, String password) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
    }

    @Override
    public User updateProfile(String currentUsername, String newUsername, String email) {

        User user = userRepository.findByUsernameIgnoreCase(currentUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setUsername(newUsername);
        user.setEmail(email);

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String id){
        userRepository.deleteById(id);
    }

    @Override
    public User getCurrentUser() {
        VaadinSession session = VaadinSession.getCurrent();

        if (session == null) {
            return null;
        }

        return session.getAttribute(User.class);
    }

    @Override
    public void logout() {
        VaadinSession.getCurrent().setAttribute(User.class, null);
    }
}
