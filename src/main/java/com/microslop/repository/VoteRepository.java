package com.microslop.repository;

import com.microslop.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    /** Checks if a user has already voted for a specific project. */
    boolean existsByUserUsernameAndProjectId(String username, Long projectId);

    /** Total votes received by a project. */
    long countByProjectId(Long projectId);

    /** Total votes cast by a user in an entire competition. */
    @Query("""
        SELECT COUNT(v) FROM Vote v
        WHERE v.user.id = :username
        AND v.project.competition.id = :competitionId
        """)
    long countByUserInCompetition(@Param("username") String username,
                                  @Param("competitionId") Long competitionId);
}
