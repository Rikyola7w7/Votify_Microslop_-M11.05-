package com.microslop.repository;

import com.microslop.entity.ChecklistVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistVoteRepository extends JpaRepository<ChecklistVote, Long> {

    /** Total checked items for a project (for ranking). */
    long countByProjectId(Long projectId);

    /** Items checked by a user for a specific project. */
    long countByUserIdAndProjectId(Long userId, Long projectId);

    /** Whether a user has already checked a specific item for a project. */
    boolean existsByUserIdAndProjectIdAndChecklistItemId(Long userId, Long projectId, Long checklistItemId);

    /** Total checklist actions by a user in a competition. */
    @Query("""
        SELECT COUNT(cv) FROM ChecklistVote cv
        WHERE cv.user.id = :userId
        AND cv.project.competition.id = :competitionId
        """)
    long countByUserIdAndCompetitionId(@Param("userId") Long userId,
                                       @Param("competitionId") Long competitionId);

    /** Delete all votes for a specific checklist item. */
    @Modifying
    long deleteByChecklistItem_Id(Long checklistItemId);
}
