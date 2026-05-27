package com.microslop.repository;

import com.microslop.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.categories WHERE p.competition.id = :competitionId")
    List<Project> findByCompetitionId(@Param("competitionId") Long competitionId);

    @Query("""
        SELECT DISTINCT p FROM Project p
        LEFT JOIN FETCH p.categories
        LEFT JOIN FETCH p.competition
        INNER JOIN p.participants u
        WHERE u.username = :username
        """)
    List<Project> findProjectsByParticipantUsername(@Param("username") String username);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.votes v WHERE p.competition.id = :competitionId GROUP BY p ORDER BY COUNT(v) DESC")
    List<Project> findRankingByCompetition(@Param("competitionId") Long competitionId);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.votes v LEFT JOIN FETCH p.categories c WHERE c.id = :categoryId GROUP BY p ORDER BY COUNT(v) DESC")
    List<Project> findRankingByCategory(@Param("categoryId") Long categoryId);

    // Projects of a competition ordered by checklist vote count
    @Query("""
        SELECT p FROM Project p
        WHERE p.competition.id = :competitionId
        ORDER BY (
            SELECT COUNT(cv) FROM ChecklistVote cv WHERE cv.project.id = p.id
        ) DESC
        """)
    List<Project> findRankingByChecklistCompetition(@Param("competitionId") Long competitionId);

    // Projects in a category ordered by checklist vote count
    @Query("""
        SELECT p FROM Project p
        JOIN p.categories c
        WHERE c.id = :categoryId
        ORDER BY (
            SELECT COUNT(cv) FROM ChecklistVote cv WHERE cv.project.id = p.id
        ) DESC
        """)
    List<Project> findChecklistRankingByCategory(@Param("categoryId") Long categoryId);

    // Judge-only ranking: projects ordered by count of votes from judges
    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.categories c
        LEFT JOIN p.votes v
        LEFT JOIN com.microslop.entity.Judge j ON j.user = v.user AND j.competition = p.competition
        WHERE c.id = :categoryId
        GROUP BY p
        ORDER BY COUNT(CASE WHEN j.id IS NOT NULL THEN v.id END) DESC, p.id ASC
        """)
    List<Project> findJudgeRankingByCategory(@Param("categoryId") Long categoryId);

    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.categories c
        LEFT JOIN p.votes v
        LEFT JOIN com.microslop.entity.Judge j ON j.user = v.user AND j.competition = p.competition
        WHERE c.id = :categoryId
        GROUP BY p
        ORDER BY COUNT(CASE WHEN j.id IS NULL THEN v.id END) DESC, p.id ASC
        """)
    List<Project> findPopularRankingByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.votes v LEFT JOIN FETCH v.user WHERE p.id = :projectId")
    Optional<Project> findByIdWithVotesAndUsers(@Param("projectId") Long projectId);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.votes LEFT JOIN FETCH p.categories c WHERE c.id = :categoryId")
    List<Project> findAllByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT DISTINCT p FROM Project p JOIN FETCH p.categories c WHERE p.competition.id = :competitionId AND c.id = :categoryId")
    List<Project> findByCompetitionIdAndCategoryId(@Param("competitionId") Long competitionId, @Param("categoryId") Long categoryId);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.categories WHERE p.competition.id = :competitionId AND p.customPosition IS NOT NULL")
    List<Project> findByCompetitionIdAndCustomPositionIsNotNull(@Param("competitionId") Long competitionId);

    @Query("""
        SELECT p FROM Project p
        LEFT JOIN FETCH p.votes
        LEFT JOIN p.categories c
        WHERE c.id = :categoryId
        """)
    List<Project> findAllProjectsByCategoryId(@Param("categoryId") Long categoryId);

    @Modifying
    @Query("UPDATE Project p SET p.customPosition = NULL, p.manualVoteCount = NULL " +
           "WHERE p.id IN (SELECT p2.id FROM Project p2 JOIN p2.categories c WHERE c.id = :categoryId)")
    void clearModificationsByCategoryId(@Param("categoryId") Long categoryId);

    // Projects of a competition ordered by average scale score (descending)
    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.votes v
        WHERE p.competition.id = :competitionId
        GROUP BY p
        ORDER BY COALESCE(AVG(v.points), 0) DESC
        """)
    List<Project> findRankingByScaleCompetition(@Param("competitionId") Long competitionId);

    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.votes v
        LEFT JOIN p.categories c
        WHERE c.id = :categoryId
        GROUP BY p
        ORDER BY COALESCE(AVG(v.points), 0) DESC
        """)
    List<Project> findRankingByScaleCategory(@Param("categoryId") Long categoryId);
}
