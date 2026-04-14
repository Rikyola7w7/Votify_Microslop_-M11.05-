package com.microslop.service;

import com.microslop.entity.User;
import java.util.Optional;

public interface UserService {
    void registerUser(User newUser);

    void login(String username, String password);

    User updateProfile(String currentUsername, String newUsername, String newEmail);

    Optional<User> searchByUsernameIgnoreCase(String username);

    void deleteUser(String username);

    User getCurrentUser();

    void logout();

    String getUserDisplayName();
}
