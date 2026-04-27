package com.microslop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_comment")
@Data
@NoArgsConstructor
@ToString(exclude = {"project", "user"})
public class ProjectComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "comment_text", nullable = false, length = 2000)
    private String commentText;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    public ProjectComment(Project project, User user, String commentText) {
        this.project = project;
        this.user = user;
        this.commentText = commentText;
        this.creationDate = LocalDateTime.now();
    }

    public String getUsername() { 
        return user != null ? user.getUsername() : null; 
    }

    public void setUsername(String username) { 
        /* Campo deprecated - usar setUser() */ 
    }
}
