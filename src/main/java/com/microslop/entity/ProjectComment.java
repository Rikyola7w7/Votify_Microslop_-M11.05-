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

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "comment_text", nullable = false, length = 2000)
    private String commentText;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    public ProjectComment() {
        this.creationDate = LocalDateTime.now();
    }

    public ProjectComment(Project project, String username, String commentText) {
        this.project = project;
        this.username = username;
        this.commentText = commentText;
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    @Override
    public String toString() {
        return "ProjectComment{" +
                "id=" + id +
                ", project=" + project +
                ", username='" + username + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
