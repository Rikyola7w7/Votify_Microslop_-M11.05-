package com.microslop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a certificate type (e.g., Participant, Judge Winner, Popular Winner)
 * Replaces the CertificateType enum for better OOP design and flexibility
 */
@Entity
@Table(name = "certificate_type_entity", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"}, name = "uk_certificate_type_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(length = 255)
    private String description;

    @Column(length = 7)
    private String colorCode; // Hex color code for UI (e.g., #4a90e2)

    @Column(length = 50)
    private String iconType; // Icon name for UI (e.g., PARTICIPANT, TROPHY, STAR)

    @OneToMany(mappedBy = "certificateType", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Certificate> certificates = new ArrayList<>();

    /**
     * Constructor for creating a certificate type with essential fields
     */
    public CertificateTypeEntity(String code, String displayName, String description, String colorCode, String iconType) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
        this.iconType = iconType;
    }

    /**
     * Get the display name for the certificate type
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this is a participant certificate type
     */
    public boolean isParticipant() {
        return "PARTICIPANT".equalsIgnoreCase(code);
    }

    /**
     * Check if this is a winner certificate type
     */
    public boolean isWinner() {
        return isJudgeWinner() || isPopularWinner();
    }

    /**
     * Check if this is a judge winner certificate type
     */
    public boolean isJudgeWinner() {
        return "JUDGE_WINNER".equalsIgnoreCase(code);
    }

    /**
     * Check if this is a popular winner certificate type
     */
    public boolean isPopularWinner() {
        return "POPULAR_WINNER".equalsIgnoreCase(code);
    }
}
