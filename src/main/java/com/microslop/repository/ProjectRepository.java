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

// Projects of a competition ordered by checklist vote count
    @Query("""
        SELECT p FROM Project p
        WHERE p.competition.id = :competitionId
        ORDER BY (
            SELECT COUNT(cv) FROM ChecklistVote cv WHERE cv.project.id = p.id
        ) DESC
        """)
    List<Project> findRankingByChecklistCompetition(@Param("competitionId") Long competitionId);

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

    // Projects of a competition ordered by average scale score (descending)
    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.votes v
        WHERE p.competition.id = :competitionId
        GROUP BY p
        ORDER BY COALESCE(AVG(v.points), 0) DESC
        """)
    List<Project> findRankingByScaleCompetition(@Param("competitionId") Long competitionId);

    // Projects with a specific category ordered by average scale score (descending)
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
