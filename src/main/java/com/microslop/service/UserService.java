package com.microslop.service;

import com.microslop.entity.User;

import java.util.Optional;

public interface UserService {
    User register(String username, String email, String password);

    Optional<User> login(String username, String password);

    User updateProfile(Long id, String username, String email);

    Optional<User> searchById(Long id);

    Optional<User> searchByUsername(String username);

    void deleteUser(Long id);

    public User getCurrentUser();

    public void logout();
}
