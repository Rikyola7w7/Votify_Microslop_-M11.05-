package com.microslop.entity;

import com.microslop.state.CompetitionState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competition")
@Data
@NoArgsConstructor
@ToString(exclude = {"projects", "categories", "judges"})
@BatchSize(size = 50)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CompetitionStatus status = CompetitionStatus.DRAFT;

    @Column(name = "event_type", length = 100)
    private String eventType;

    @Lob
    @Column(name = "cover_image")
    private byte[] coverImage;

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

    @Column(name = "voting_strategy_type", length = 50)
    private String votingStrategyType = "ALL";

    @Column(name = "ranking_strategy_type", length = 50)
    private String rankingStrategyType = "AVERAGE";

    public static com.microslop.builder.CompetitionBuilder builder() {
        return com.microslop.builder.CompetitionBuilder.builder();
    }

    // ── Comments Configuration ─────────────────────────────────────────────
    @Column(name = "comments_enabled", columnDefinition = "boolean default true")
    private Boolean commentsEnabled = true;

    @Column(name = "comments_required", columnDefinition = "boolean default false")
    private Boolean commentsRequired = false;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
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

    // ── State Pattern Methods ─────────────────────────────────────────────

    /**
     * Delegates to current state object to transition to ACTIVE.
     */
    public void activate() {
        this.status.getState().activate(this);
    }

    /**
     * Delegates to current state object to deactivate (back to DRAFT).
     */
    public void deactivate() {
        this.status.getState().deactivate(this);
    }

    /**
     * Delegates to current state object to open voting.
     */
    public void openVoting() {
        this.status.getState().openVoting(this);
    }

    /**
     * Delegates to current state object to pause voting.
     */
    public void pauseVoting() {
        this.status.getState().pauseVoting(this);
    }

    /**
     * Delegates to current state object to conclude.
     */
    public void conclude() {
        this.status.getState().conclude(this);
    }

    /**
     * Delegates to current state object to archive.
     */
    public void archive() {
        this.status.getState().archive(this);
    }

    /**
     * Delegates to current state object to reopen.
     */
    public void reopen() {
        this.status.getState().reopen(this);
    }

    /**
     * Whether voting is currently allowed based on state.
     */
    public boolean canVote() {
        return status != null && status.getState().canVote();
    }

    /**
     * Whether projects can be submitted based on state.
     */
    public boolean canSubmitProjects() {
        return status != null && status.getState().canSubmitProjects();
    }

    /**
     * Whether configuration can be edited based on state.
     */
    public boolean canEditConfiguration() {
        return status != null && status.getState().canEditConfiguration();
    }

    /**
     * Whether this state is terminal (no further transitions).
     */
    public boolean isTerminal() {
        return status != null && status.getState().isTerminal();
    }

    /**
     * Computed property: derives active status from the current state.
     * Replaces the direct boolean field for reads.
     */
    public boolean isActive() {
        return status != null && status.getState().isActiveLegacy();
    }

    /**
     * @deprecated Use state transition methods (activate, deactivate, etc.) instead.
     * Kept for backward compatibility. Sets status to ACTIVE or DRAFT.
     */
    @Deprecated
    public void setActive(boolean active) {
        if (active) {
            if (this.status == null || this.status == CompetitionStatus.DRAFT
                    || this.status == CompetitionStatus.CONCLUDED) {
                this.status = CompetitionStatus.ACTIVE;
            }
        } else {
            if (this.status == CompetitionStatus.ACTIVE
                    || this.status == CompetitionStatus.PAUSED) {
                this.status = CompetitionStatus.DRAFT;
            }
        }
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

    public String getVotingStrategyType() {
        return votingStrategyType;
    }

    public void setVotingStrategyType(String votingStrategyType) {
        this.votingStrategyType = votingStrategyType;
    }

    public String getRankingStrategyType() {
        return rankingStrategyType;
    }

    public void setRankingStrategyType(String rankingStrategyType) {
        this.rankingStrategyType = rankingStrategyType;
    }
}
