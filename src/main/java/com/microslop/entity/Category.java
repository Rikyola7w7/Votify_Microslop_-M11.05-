package com.microslop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"competition_id", "name"}, name = "uk_categoty_name_per_competition")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(name = "voter_type", length = 50)
    private String voterType = "NORMAL";

    @Column(name = "vote_type", length = 50)
    private String voteType = "NORMAL"; // NORMAL, CHECKLIST, SCALE

    @Lob
    @Column(name = "image")
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProjectComment> projectComments = new ArrayList<>();

    /**
     * Check if this category uses checklist-based voting.
     */
    public boolean isChecklistVoting() {
        return "CHECKLIST".equalsIgnoreCase(voteType);
    }
}
