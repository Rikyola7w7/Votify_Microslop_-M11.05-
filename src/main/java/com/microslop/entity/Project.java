package com.microslop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@ToString(exclude = {"participants", "votes", "comments", "categories"})
@BatchSize(size = 50)
public class Project {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(length = 2000, name = "description")
    private String description;

    @Column(name = "custom_position")
    private Integer customPosition;

    @Column(name = "manual_vote_count")
    private Integer manualVoteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_project",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @BatchSize(size = 50)
    private List<User> participants = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private List<ProjectComment> comments = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "project_category",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @BatchSize(size = 50)
    private List<Category> categories = new ArrayList<>();

    public Project(String name, String description, Competition competition) {
        this.name       = name;
        this.description  = description;
        this.competition  = competition;
    }

    public static com.microslop.builder.ProjectBuilder builder() {
        return com.microslop.builder.ProjectBuilder.builder();
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

    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }
}
