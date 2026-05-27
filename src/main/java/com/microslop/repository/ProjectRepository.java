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

    // Find projects by competition ID
    List<Project> findByCompetitionId(Long competitionId);

    // Find projects where a user is a participant
    @Query("""
        SELECT p FROM Project p
        INNER JOIN p.participants u
        WHERE u.username = :username
        """)
    List<Project> findProjectsByParticipantUsername(@Param("username") String username);

    // Projects of a competition ordered in descending order by number of votes
    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.votes v
        WHERE p.competition.id = :competitionId
        GROUP BY p
        ORDER BY COUNT(v) DESC
        """)
    List<Project> findRankingByCompetition(@Param("competitionId") Long competitionId);

    // Projects with a specific category ordered by number of votes
    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.votes v
        LEFT JOIN p.categories c
        WHERE c.id = :categoryId
        GROUP BY p
        ORDER BY COUNT(v) DESC
        """)
    List<Project> findRankingByCategory(@Param("categoryId") Long categoryId);

    // Judge-only ranking: projects ordered by count of votes from judges
    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.votes v
        LEFT JOIN p.categories c
        LEFT JOIN com.microslop.entity.Judge j ON j.user = v.user AND j.competition = p.competition
        WHERE c.id = :categoryId AND j.id IS NOT NULL
        GROUP BY p
        ORDER BY COUNT(v) DESC
        """)
    List<Project> findJudgeRankingByCategory(@Param("categoryId") Long categoryId);

    // Popular ranking: projects ordered by count of votes from non-judges
    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.votes v
        LEFT JOIN p.categories c
        LEFT JOIN com.microslop.entity.Judge j ON j.user = v.user AND j.competition = p.competition
        WHERE c.id = :categoryId AND j.id IS NULL AND v.id IS NOT NULL
        GROUP BY p
        ORDER BY COUNT(v) DESC
        """)
    List<Project> findPopularRankingByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.votes v LEFT JOIN FETCH v.user WHERE p.id = :projectId")
    Optional<Project> findByIdWithVotesAndUsers(@Param("projectId") Long projectId);

    @Query("""
        SELECT p FROM Project p
        LEFT JOIN FETCH p.votes
        LEFT JOIN p.categories c
        WHERE c.id = :categoryId
        """)
    List<Project> findAllByCategoryId(@Param("categoryId") Long categoryId);

    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.categories c
        WHERE c.id = :categoryId
        ORDER BY p.id ASC
        """)
    List<Project> findAllProjectsByCategoryId(@Param("categoryId") Long categoryId);

    @Modifying
    @Query("UPDATE Project p SET p.customPosition = NULL, p.manualVoteCount = NULL " +
           "WHERE p.id IN (SELECT p2.id FROM Project p2 JOIN p2.categories c WHERE c.id = :categoryId)")
    void clearModificationsByCategoryId(@Param("categoryId") Long categoryId);
}
