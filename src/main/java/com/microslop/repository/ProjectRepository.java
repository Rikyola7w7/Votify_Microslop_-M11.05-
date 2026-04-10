package com.microslop.repository;

import com.microslop.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

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
}
