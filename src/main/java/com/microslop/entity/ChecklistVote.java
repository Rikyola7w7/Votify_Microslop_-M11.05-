package com.microslop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "checklist_vote",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"user_id", "project_id", "checklist_item_id"},
            name = "uk_checklist_vote_user_project_item"
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_date", nullable = false)
    private LocalDateTime voteDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_item_id", nullable = false)
    private ChecklistItem checklistItem;

    public ChecklistVote(User user, Project project, ChecklistItem checklistItem) {
        this.user = user;
        this.project = project;
        this.checklistItem = checklistItem;
        this.voteDate = LocalDateTime.now();
    }
}
