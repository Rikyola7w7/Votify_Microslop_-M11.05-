package com.microslop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_date", nullable = false)
    private LocalDateTime voteDate;

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "points", nullable = false)
    private Integer points = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Vote(User user, Project project, Category category) {
        this.user      = user;
        this.project   = project;
        this.category  = category;
        this.voteDate  = LocalDateTime.now();
        this.points    = 1;
    }

    public Vote(User user, Project project, Category category, Integer points) {
        this.user      = user;
        this.project   = project;
        this.category  = category;
        this.voteDate  = LocalDateTime.now();
        this.points    = points != null ? points : 1;
    }
}