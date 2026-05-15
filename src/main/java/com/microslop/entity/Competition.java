package com.microslop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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

    // ── Voting Configuration ───────────────────────────────────────────────
    @Column(name = "voter_type", length = 50)
    private String voterType = "ALL"; // JUDGES, ALL

    @Column(name = "auto_vote", columnDefinition = "boolean default false")
    private Boolean autoVote = false;

    @Column(name = "max_votes_per_person", columnDefinition = "integer default 1")
    @Min(value = 1, message = "Max votes per person must be at least 1")
    private Integer maxVotesPerPerson = 1;

    @Column(name = "judge_weight_multiplier", columnDefinition = "double default 1.0")
    private Double judgeWeightMultiplier = 1.0;

    @Column(name = "standard_user_weight_multiplier", columnDefinition = "double default 1.0")
    private Double standardUserWeightMultiplier = 1.0;

    @Column(name = "vote_type", length = 20)
    private String voteType = "NORMAL"; // NORMAL, CHECKLIST, SCALE

    @Column(name = "scale_min")
    private Integer scaleMin = 0;

    @Column(name = "scale_max")
    private Integer scaleMax = 10;

    public static com.microslop.builder.CompetitionBuilder builder() {
        return com.microslop.builder.CompetitionBuilder.builder();
    }

    // ── Comments Configuration ─────────────────────────────────────────────
    @Column(name = "comments_enabled", columnDefinition = "boolean default true")
    private Boolean commentsEnabled = true;

    @Column(name = "comments_required", columnDefinition = "boolean default false")
    private Boolean commentsRequired = false;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Judge> judges = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItem> checklistItems = new ArrayList<>();

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

    public void addChecklistItem(ChecklistItem item) {
        checklistItems.add(item);
        item.setCompetition(this);
    }

    public void removeChecklistItem(ChecklistItem item) {
        checklistItems.remove(item);
        item.setCompetition(null);
    }

    // ── Getters and Setters for Voting Configuration ────────────────────
    public String getVoterType() {
        return voterType;
    }

    public void setVoterType(String voterType) {
        this.voterType = voterType;
    }

    public boolean isAutoVote() {
        return autoVote != null && autoVote;
    }

    public void setAutoVote(Boolean autoVote) {
        this.autoVote = autoVote;
    }

    public Integer getMaxVotesPerPerson() {
        return maxVotesPerPerson;
    }

    public void setMaxVotesPerPerson(Integer maxVotesPerPerson) {
        this.maxVotesPerPerson = maxVotesPerPerson;
    }

    public Double getJudgeWeightMultiplier() {
        return judgeWeightMultiplier;
    }

    public void setJudgeWeightMultiplier(Double judgeWeightMultiplier) {
        this.judgeWeightMultiplier = judgeWeightMultiplier;
    }

    public Double getStandardUserWeightMultiplier() {
        return standardUserWeightMultiplier;
    }

    public void setStandardUserWeightMultiplier(Double standardUserWeightMultiplier) {
        this.standardUserWeightMultiplier = standardUserWeightMultiplier;
    }

    public String getVoteType() {
        return voteType;
    }

    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }
}
