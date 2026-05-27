package com.microslop.service;

import com.microslop.entity.User;
import java.util.Optional;

public interface UserService {
    void registerUser(User newUser);

    void login(String username, String password);

    User updateProfile(String currentUsername, String newUsername, String newEmail);

    Optional<User> searchByUsernameIgnoreCase(String username);

    /**
     * Get a user by ID.
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    Optional<User> getUserById(Long userId);

    void deleteUser(String username);

    User getCurrentUser();

    void logout();

    String getUserDisplayName();

    boolean isLoggedIn();

    String getCurrentUsername();

    long getCurrentUserId();

    boolean verifyCurrentPassword(String password);
}
