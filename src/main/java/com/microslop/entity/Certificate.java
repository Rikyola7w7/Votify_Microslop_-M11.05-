package com.microslop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Year;

/**
 * Entity representing a certificate issued to a user
 * Can be either a participation certificate or a winner certificate
 * Now uses entity-based types instead of enums for better OOP design
 */
@Entity
@Table(name = "certificate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "certificate_type_id", nullable = false)
    private CertificateTypeEntity certificateType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ranking_type_id", nullable = false)
    private RankingTypeEntity rankingType;

    @Column(name = "position")
    private Integer position;  // null for PARTICIPANT, 1 for WINNER

    @Column(name = "generated_date", nullable = false)
    private LocalDateTime generatedDate;

    /**
     * Constructor for creating a participant certificate
     */
    public Certificate(User user, Competition competition, Project project, Category category, RankingTypeEntity rankingType) {
        this.user = user;
        this.competition = competition;
        this.project = project;
        this.category = category;
        this.rankingType = rankingType;
        this.position = null;
        this.generatedDate = LocalDateTime.now();
    }

    /**
     * Constructor for creating a winner certificate
     */
    public Certificate(User user, Competition competition, Project project, Category category, 
                      CertificateTypeEntity winnerType, RankingTypeEntity rankingType, int position) {
        this.user = user;
        this.competition = competition;
        this.project = project;
        this.category = category;
        this.certificateType = winnerType;
        this.rankingType = rankingType;
        this.position = position;
        this.generatedDate = LocalDateTime.now();
    }

    /**
     * Get the year of certificate generation
     */
    public String getCertificateYear() {
        return Year.now().toString();
    }

    /**
     * Get the display title for this certificate
     */
    public String getDisplayTitle() {
        return certificateType.getDisplayName();
    }

    /**
     * Get the ranking display text
     */
    public String getRankingDisplay() {
        return rankingType.getDisplayName();
    }

    /**
     * Check if this is a participant certificate
     */
    public boolean isParticipantCertificate() {
        return certificateType != null && certificateType.isParticipant();
    }

    /**
     * Check if this is a winner certificate
     */
    public boolean isWinnerCertificate() {
        return certificateType != null && certificateType.isWinner();
    }

    /**
     * Check if this is a judges ranking winner
     */
    public boolean isJudgesWinner() {
        return certificateType != null && certificateType.isJudgeWinner();
    }

    /**
     * Check if this is a popular ranking winner
     */
    public boolean isPopularWinner() {
        return certificateType != null && certificateType.isPopularWinner();
    }
}

