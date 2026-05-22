package com.microslop.repository;

import com.microslop.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}