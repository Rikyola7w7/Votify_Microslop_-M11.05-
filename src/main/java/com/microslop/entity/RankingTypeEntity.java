package com.microslop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a ranking type (e.g., Judges Ranking, Popular Ranking)
 * Replaces the RankingType enum for better OOP design and flexibility
 */
@Entity
@Table(name = "ranking_type_entity", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"}, name = "uk_ranking_type_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "rankingType", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Certificate> certificates = new ArrayList<>();

    /**
     * Constructor for creating a ranking type with essential fields
     */
    public RankingTypeEntity(String code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Get the display name for the ranking type
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this is a judges ranking type
     */
    public boolean isJudgesRanking() {
        return "JUDGES_RANKING".equalsIgnoreCase(code);
    }

    /**
     * Check if this is a popular ranking type
     */
    public boolean isPopularRanking() {
        return "POPULAR_RANKING".equalsIgnoreCase(code);
    }
}
