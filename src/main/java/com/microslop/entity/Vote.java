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
    private LocalDateTime fechaVoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public Vote() {
        this.fechaVoto = LocalDateTime.now();
    }

    public Vote(User user, Project project) {
        this.user   = user;
        this.project  = project;
        this.fechaVoto = LocalDateTime.now();
    }

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public LocalDateTime getFechaVoto()        { return fechaVoto; }
    public void setFechaVoto(LocalDateTime f)  { this.fechaVoto = f; }

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
                ", fechaVoto=" + fechaVoto +
                '}';
    }
}
