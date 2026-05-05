package com.microslop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competition")
@Data
@NoArgsConstructor
@ToString(exclude = {"projects", "categories", "judges"})
public class Competition {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(length = 1000, name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(nullable = false, name = "active")
    private boolean active = true;

    @Column(name = "event_type", length = 100)
    private String eventType;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Judge> judges = new ArrayList<>();

    @Column(nullable = false, name = "max_votes")
    private int maxVotes = 1;

    public Competition(String name, String description,
                       LocalDateTime startDate, LocalDateTime endDate) {
        this.name      = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void addProject(Project project) {
        projects.add(project);
        project.setCompetition(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.setCompetition(null);
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.setCompetition(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.setCompetition(null);
    }

    public void addJudge(Judge judge) {
        judges.add(judge);
        judge.setCompetition(this);
    }

    public void removeJudge(Judge judge) {
        judges.remove(judge);
        judge.setCompetition(null);
    }
}
