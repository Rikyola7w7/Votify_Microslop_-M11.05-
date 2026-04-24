package com.microslop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competition")
public class Competition {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(length = 1000, name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startdate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(nullable = false, name = "active")
    private boolean active = true;

    @Column(name = "event_type", length = 100)
    private String eventType;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    public Competition() {
        this.createdAt = LocalDateTime.now();
    }

    public Competition(String name, String description,
                       LocalDateTime startDate, LocalDateTime endDate) {
        this.name      = name;
        this.description = description;
        this.startdate = startDate;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
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

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }

    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }

    public LocalDateTime getStartDate()         { return startdate; }
    public void setStartDate(LocalDateTime f)   { this.startdate = f; }

    public LocalDateTime getEndDate()           { return endDate; }
    public void setEndDate(LocalDateTime f)     { this.endDate = f; }

    public boolean isActive()                   { return active; }
    public void setActive(boolean active)       { this.active = active; }

    public String getEventType()                { return eventType; }
    public void setEventType(String eventType)  { this.eventType = eventType; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getCreator()                    { return creator; }
    public void setCreator(User creator)        { this.creator = creator; }

    public List<Project> getProjects()          { return projects; }
    public void setProjects(List<Project> p)    { this.projects = p; }

    public List<Category> getCategories()       { return categories; }
    public void setCategories(List<Category> c) { this.categories = c; }

    @Override
    public String toString() {
        return "Competition{id=" + id + ", name='" + name + "', active=" + active + ", eventType='" + eventType + "'}";
    }
}
