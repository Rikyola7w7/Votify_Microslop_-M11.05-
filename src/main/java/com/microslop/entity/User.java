package com.microslop.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime; 

@Entity
@Table(name = "users")
public class User {

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, unique = true, name = "email")
    private String email;

    @Column(nullable = false)
    private LocalDateTime creation_date;

    @Id
    @Column(nullable = false, unique = true, name = "username")
    private String username;

    @Column(nullable = false, name = "password")
    private String password;

    @Lob
    @Column(nullable = true, name = "profile_picture")
    private byte[] profile_picture;

    @Column(nullable = false, name = "birth_date")
    private LocalDateTime birth_date;

    public User() {
        this.creation_date = LocalDateTime.now();
    }

    public User(String nombre, String email, String username, String password, LocalDateTime fechaNacimiento) {
        this.name = nombre;
        this.username = username;
        this.password = password;
        this.email = email;
        this.creation_date = LocalDateTime.now();
        this.birth_date = fechaNacimiento;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreation_date() { return creation_date; }
    public void setCreation_date(LocalDateTime creation_date) { this.creation_date = creation_date; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public byte[] getProfile_picture() { return profile_picture; }
    public void setProfile_picture(byte[] profile_picture) { this.profile_picture = profile_picture; }

    public LocalDateTime getBirth_date() { return birth_date; }
    public void setBirth_date(LocalDateTime birth_date) { this.birth_date = birth_date; }

    @Override
    public String toString() {
        return "User{" + "username=" + username +
                ", name=" + name +
                ", email=" + email +
                ", creation_date=" + creation_date +
                ", birth_date=" + birth_date +
                '}';
    }
}
