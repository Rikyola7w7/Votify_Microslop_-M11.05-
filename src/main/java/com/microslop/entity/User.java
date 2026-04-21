package com.microslop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
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


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }

    public List<ProjectComment> getComments() { return comments; }
    public void setComments(List<ProjectComment> comments) { this.comments = comments; }

    @Override
    public String toString() {
        return "User{" + "username=" + username +
                ", name=" + name +
                ", email=" + email +
                ", creationDate=" + creationDate +
                ", birthDate=" + birthDate +
                '}';
    }
}