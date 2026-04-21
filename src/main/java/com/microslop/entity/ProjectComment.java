package com.microslop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_comment")
public class ProjectComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "comment_text", nullable = false, length = 2000)
    private String commentText;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    public ProjectComment() {
        this.creationDate = LocalDateTime.now();
    }

    public ProjectComment(Project project, User user, String commentText) {
        this.project = project;
        this.user = user;
        this.commentText = commentText;
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getUsername() { return user != null ? user.getUsername() : null; }
    public void setUsername(String username) { /* Campo deprecated - usar setUser() */ }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    @Override
    public String toString() {
        return "ProjectComment{" +
                "id=" + id +
                ", project=" + project +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", creationDate=" + creationDate +
                '}';
    }
}
