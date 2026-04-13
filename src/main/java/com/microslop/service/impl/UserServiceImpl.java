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
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        
        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
    }

    @Override
    public User updateProfile(String currentUsername, String newUsername, String email) {

        User user = userRepository.findByUsernameIgnoreCase(currentUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        User newUser = new User();
        newUser.setUsername(newUsername);
        newUser.setEmail(email);
        newUser.setPassword(user.getPassword());
        newUser.setBirthDate(user.getBirthDate());
        newUser.setCreationDate(user.getCreationDate());
        newUser.setName(user.getName());
        newUser.setProfilePicture(user.getProfilePicture());

        userRepository.delete(user);

        User savedUser = userRepository.save(newUser);
        VaadinSession.getCurrent().setAttribute(User.class, savedUser);

        return savedUser;
    }

    @Override
    public void deleteUser(String username){
        userRepository.deleteById(username);
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
