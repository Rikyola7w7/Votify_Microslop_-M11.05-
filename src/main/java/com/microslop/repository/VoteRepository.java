package com.microslop.repository;

import com.microslop.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    /** Total votes received by a project. */
    long countByProjectId(Long projectId);

    /** Total votes cast by a user for a specific project. */
    long countByUserIdAndProjectId(Long userId, Long projectId);

    /** Total votes cast by a user in an entire competition. */
    @Query("""
        SELECT COUNT(v) FROM Vote v
        WHERE v.user.id = :userId
        AND v.project.competition.id = :competitionId
        """)
    long countByUserInCompetition(@Param("userId") Long userId,
                                  @Param("competitionId") Long competitionId);

    /** Total votes cast by a user for a specific category. */
    long countByUserIdAndCategoryId(Long userId, Long categoryId);
}