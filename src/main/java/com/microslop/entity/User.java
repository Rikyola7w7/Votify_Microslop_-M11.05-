package com.microslop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@ToString(exclude = {"votes", "comments", "password", "notifications"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private byte[] profilePicture;

    @Column(nullable = false)
    private LocalDateTime birthDate;    

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<ProjectComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    public User() {
        this.creationDate = LocalDateTime.now();
    }

    public User(String name, String email, String username, String password, LocalDateTime birthDate) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.creationDate = LocalDateTime.now();
        this.birthDate = birthDate;
    }

    public static com.microslop.builder.UserBuilder builder() {
        return com.microslop.builder.UserBuilder.builder();
    }
}