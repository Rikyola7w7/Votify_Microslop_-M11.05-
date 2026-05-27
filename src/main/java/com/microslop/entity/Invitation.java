package com.microslop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Data
@NoArgsConstructor
public class Invitation {

    public enum InvitationStatus {
        PENDING, ACCEPTED, REFUSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invited_by_id", nullable = false)
    private User invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    public Invitation(User user, Long projectId, String projectName, Competition competition, User invitedBy) {
        this.user = user;
        this.projectId = projectId;
        this.projectName = projectName;
        this.competition = competition;
        this.invitedBy = invitedBy;
        this.status = InvitationStatus.PENDING;
        this.creationDate = LocalDateTime.now();
    }

    public Invitation(User user, Long projectId, String projectName, Competition competition, User invitedBy, LocalDateTime expirationDate) {
        this(user, projectId, projectName, competition, invitedBy);
        this.expirationDate = expirationDate;
    }

    public boolean isExpired() {
        return expirationDate != null && LocalDateTime.now().isAfter(expirationDate);
    }

    public boolean isPending() {
        return status == InvitationStatus.PENDING && !isExpired();
    }
}
