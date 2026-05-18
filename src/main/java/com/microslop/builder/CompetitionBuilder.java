package com.microslop.builder;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import java.time.LocalDateTime;

public class CompetitionBuilder {
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CompetitionStatus status = CompetitionStatus.DRAFT;
    private String eventType;
    private String createdBy;
    private String voterType = "ALL";
    private Boolean autoVote = false;
    private Integer maxVotesPerPerson = 1;

    public static CompetitionBuilder builder() {
        return new CompetitionBuilder();
    }

    public CompetitionBuilder name(String name) {
        this.name = name;
        return this;
    }
    public CompetitionBuilder description(String description) {
        this.description = description;
        return this;
    }
    public CompetitionBuilder startDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }
    public CompetitionBuilder endDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }
    public CompetitionBuilder status(CompetitionStatus status) {
        this.status = status;
        return this;
    }
    @Deprecated
    public CompetitionBuilder active(boolean active) {
        this.status = active ? CompetitionStatus.ACTIVE : CompetitionStatus.DRAFT;
        return this;
    }
    public CompetitionBuilder eventType(String eventType) {
        this.eventType = eventType;
        return this;
    }
    public CompetitionBuilder createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }
    public CompetitionBuilder voterType(String voterType) {
        this.voterType = voterType;
        return this;
    }
    public CompetitionBuilder autoVote(Boolean autoVote) {
        this.autoVote = autoVote;
        return this;
    }
    public CompetitionBuilder maxVotesPerPerson(Integer maxVotesPerPerson) {
        this.maxVotesPerPerson = maxVotesPerPerson;
        return this;
    }
    public Competition build() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Competition name cannot be empty.");
        }
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        Competition competition = new Competition();
        competition.setName(name);
        competition.setDescription(description);
        competition.setStartDate(startDate);
        competition.setEndDate(endDate);
        competition.setStatus(status);
        competition.setEventType(eventType);
        competition.setCreatedBy(createdBy);
        competition.setVoterType(voterType);
        competition.setAutoVote(autoVote);
        competition.setMaxVotesPerPerson(maxVotesPerPerson);
        return competition;
    }
}
