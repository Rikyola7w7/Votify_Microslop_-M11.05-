package com.microslop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "vote", 
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_project",
        columnNames = {"username", "project_id"}
    )
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_date", nullable = false)
    private LocalDateTime voteDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public Vote() {
        this.voteDate = LocalDateTime.now();
    }

    public Vote(User user, Project project) {
        this.user   = user;
        this.project  = project;
        this.voteDate = LocalDateTime.now();
    }

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public LocalDateTime getVoteDate()         { return voteDate; }
    public void setVoteDate(LocalDateTime f)   { this.voteDate = f; }

    public User getUser()                { return user; }
    public void setUser(User u)          { this.user = u; }

    public Project getProject()              { return project; }
    public void setProject(Project p)        { this.project = p; }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", user=" + user +
                ", project=" + project +
                ", voteDate=" + voteDate +
                '}';
    }
}
