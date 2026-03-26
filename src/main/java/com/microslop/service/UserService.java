package com.microslop.service;

import com.microslop.entity.User;
import java.util.Optional;

public interface UserService {
    void registerUser(User newUser);

    void login(String username, String password);

    User updateProfile(Long id, String username, String email);
    Optional<User> searchByUsernameIgnoreCase(String username);
}
