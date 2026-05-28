package com.microslop.service.impl;

import com.microslop.context.VaadinSessionContext;
import com.microslop.entity.User;
import com.microslop.exception.BusinessValidationException;
import com.microslop.exception.EntityNotFoundException;
import com.microslop.repository.UserRepository;
import com.microslop.service.UserService;
import com.microslop.command.CommandExecutor;
import com.microslop.command.user.UpdateUserProfileCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommandExecutor commandExecutor;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                         CommandExecutor commandExecutor) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.commandExecutor = commandExecutor;
    }

    @Transactional
    public void registerUser(User newUser) {
        if (userRepository.existsByUsernameIgnoreCase(newUser.getUsername())) {
            throw new BusinessValidationException("Username is already in use. Choose another one.");
        }

        if (userRepository.existsByEmailIgnoreCase(newUser.getEmail())) {
            throw new BusinessValidationException("An account with this email already exists.");
        }

        if (newUser.getPassword() == null) {
            throw new BusinessValidationException("Password cannot be null.");
        }

        if (newUser.getPassword().length() < 6) {
            throw new BusinessValidationException("Password must be at least 6 characters long.");
        }

        if (LocalDate.now().isBefore(newUser.getBirthDate().toLocalDate())) {
            throw new BusinessValidationException("Birth date cannot be in the future.");
        }

        if (LocalDate.now().minusYears(13).isBefore(newUser.getBirthDate().toLocalDate())) {
            throw new BusinessValidationException("You must be at least 13 years old to register.");
        }

        String encryptedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encryptedPassword);
        userRepository.save(newUser);
        log.info("User registered successfully: {}", newUser.getUsername());
    }

    @Override
    public Optional<User> searchByUsernameIgnoreCase(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public void login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if (!userOptional.isPresent()) {
            throw new BusinessValidationException("Invalid username or password.");
        }
        
        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessValidationException("Invalid username or password.");
        }
        log.info("User logged in: {}", username);
    }

    @Override
    public User updateProfile(String currentUsername, String newUsername, String email) {
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
            currentUsername, newUsername, email,
            userRepository
        );
        try {
            commandExecutor.execute(command);
            User updatedUser = command.getLastResult();
            VaadinSessionContext.setCurrentUser(updatedUser);
            log.info("User profile updated: {}", currentUsername);
            return updatedUser;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user profile", e);
        }
    }

    @Override
    public void deleteUser(String username){
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new EntityNotFoundException("User", username));
        userRepository.deleteById(user.getId());
        log.info("User deleted: {}", username);
    }

    @Override
    public User getCurrentUser() {
        return VaadinSessionContext.getCurrentUser();
    }

    @Override
    public void logout() {
        VaadinSessionContext.logout();
    }

    @Override
    public String getUserDisplayName() {
        return VaadinSessionContext.getUserDisplayName();
    }

    @Override
    public boolean isLoggedIn() {
        return VaadinSessionContext.isLoggedIn();
    }

    @Override
    public String getCurrentUsername() {
        return VaadinSessionContext.getCurrentUsername();
    }

    @Override
    public long getCurrentUserId() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser.getId();
        }
        throw new IllegalStateException("No user is currently logged in");
    }
}
