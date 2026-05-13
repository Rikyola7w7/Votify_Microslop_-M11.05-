package com.microslop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a judge for a competition.
 * Maps judges (users) to competitions in a many-to-many relationship.
 * Table: is_judge
 */
@Entity
@Table(name = "is_judge")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Judge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    public Judge(User user, Competition competition) {
        this.user = user;
        this.competition = competition;
    }
}
