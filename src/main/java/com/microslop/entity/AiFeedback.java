package com.microslop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_feedback")
@Data
@NoArgsConstructor
@ToString(exclude = {"project"})
public class AiFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "summary", nullable = false, length = 2000)
    private String summary;

    @Column(name = "positive_points", length = 4000)
    private String positivePoints; // JSON array stored as string

    @Column(name = "negative_points", length = 4000)
    private String negativePoints; // JSON array stored as string

    @Column(name = "sentiment_score")
    private Double sentimentScore;

    @Column(name = "positive_count")
    private Integer positiveCount;

    @Column(name = "neutral_count")
    private Integer neutralCount;

    @Column(name = "negative_count")
    private Integer negativeCount;

    @Column(name = "frequent_words", length = 2000)
    private String frequentWords; // JSON array stored as string

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public AiFeedback(Project project, String summary, String positivePoints,
                      String negativePoints, Double sentimentScore,
                      Integer positiveCount, Integer neutralCount,
                      Integer negativeCount, String frequentWords) {
        this.project = project;
        this.summary = summary;
        this.positivePoints = positivePoints;
        this.negativePoints = negativePoints;
        this.sentimentScore = sentimentScore;
        this.positiveCount = positiveCount;
        this.neutralCount = neutralCount;
        this.negativeCount = negativeCount;
        this.frequentWords = frequentWords;
        this.generatedAt = LocalDateTime.now();
    }
}
