package com.microslop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a registered voter for a specific category in a competition.
 * Maps users to competitions and categories in a many-to-many relationship.
 * Table: voter
 */
@Entity
@Table(name = "voter",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "competition_id", "category_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "votes_left", nullable = false)
    private int votesLeft = 1;

    public Voter(User user, Competition competition, Category category, int votesLeft) {
        this.user = user;
        this.competition = competition;
        this.category = category;
        this.votesLeft = votesLeft;
    }

    public Voter(User user, Competition competition, Category category) {
        this(user, competition, category, 1);
    }
}
