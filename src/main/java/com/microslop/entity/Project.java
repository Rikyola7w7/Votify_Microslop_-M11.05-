package com.microslop.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
public class Project {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(length = 2000, name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectComment> comments = new ArrayList<>();

    public Project() {}

    public Project(String name, String description, Competition competition) {
        this.name       = name;
        this.description  = description;
        this.competition  = competition;
    }

    public void addVote(Vote vote) {
        votes.add(vote);
        vote.setProject(this);
    }

    public void removeVote(Vote vote) {
        votes.remove(vote);
        vote.setProject(null);
    }

    public long getTotalVotes() {
        return votes.size();
    }

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getName()                      { return name; }
    public void setName(String name)             { this.name = name; }

    public String getDescription()               { return description; }
    public void setDescription(String description)  { this.description = description; }

    public Competition getCompetition()          { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }

    public List<Vote> getVotes()                 { return votes; }
    public void setVotes(List<Vote> votes)       { this.votes = votes; }

    public List<ProjectComment> getComments()    { return comments; }
    public void setComments(List<ProjectComment> comments) { this.comments = comments; }

    public void addComment(ProjectComment comment) {
        comments.add(comment);
        comment.setProject(this);
    }

    public void removeComment(ProjectComment comment) {
        comments.remove(comment);
        comment.setProject(null);
    }

    public long getCommentCount() {
        return comments.size();
    }

    @Override
    public String toString() {
        return "Project{id=" + id + ", name='" + name + "', votes=" + getTotalVotes() + ", comments=" + getCommentCount() + "}";
    }
}
