package com.microslop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@ToString(exclude = {"participants", "votes", "comments"})
public class Project {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(length = 2000, name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_project",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "username")
    )
    private List<User> participants = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProjectComment> comments = new ArrayList<>();

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

    public void addParticipant(User user) {
        if (!participants.contains(user)) {
            participants.add(user);
        }
    }

    public void removeParticipant(User user) {
        participants.remove(user);
    }

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
}
