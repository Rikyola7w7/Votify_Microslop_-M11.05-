package com.microslop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pending_project_submission")
@Data
@NoArgsConstructor
public class PendingProjectSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "project_name")
    private String projectName;

    @Column(length = 2000, name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "submitter_id", nullable = false)
    private User submitter;

    @Column(name = "category_ids")
    private String categoryIds;

    @Column(name = "invited_participant_ids")
    private String invitedParticipantIds;

    public PendingProjectSubmission(String projectName, String description, Competition competition, User submitter, String categoryIds) {
        this.projectName = projectName;
        this.description = description;
        this.competition = competition;
        this.submitter = submitter;
        this.categoryIds = categoryIds;
        this.invitedParticipantIds = "";
    }

    public PendingProjectSubmission(String projectName, String description, Competition competition, User submitter, String categoryIds, String invitedParticipantIds) {
        this.projectName = projectName;
        this.description = description;
        this.competition = competition;
        this.submitter = submitter;
        this.categoryIds = categoryIds;
        this.invitedParticipantIds = invitedParticipantIds;
    }
}
