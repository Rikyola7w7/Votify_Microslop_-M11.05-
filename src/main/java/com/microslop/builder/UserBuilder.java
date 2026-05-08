package com.microslop.builder;

import com.microslop.entity.User;
import java.time.LocalDateTime;

public class UserBuilder {
    private String name;
    private String email;
    private String username;
    private String password;
    private LocalDateTime birthDate;
    private byte[] profilePicture;

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }
    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }
    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }
    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }
    public UserBuilder birthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }
    public UserBuilder profilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
        return this;
    }
    public User build() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null.");
        }
        User user = new User(name, email, username, password, birthDate);
        if (profilePicture != null) {
            user.setProfilePicture(profilePicture);
        }
        return user;
    }
}
