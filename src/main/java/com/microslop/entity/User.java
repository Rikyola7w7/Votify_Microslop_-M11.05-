package com.microslop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@ToString(exclude = {"votes", "comments", "password"})
public class User {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private byte[] profilePicture;

    @Column(nullable = false)
    private LocalDateTime birthDate;    

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectComment> comments = new ArrayList<>();

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
}