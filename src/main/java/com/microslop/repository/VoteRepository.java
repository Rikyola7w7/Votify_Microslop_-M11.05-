package com.microslop.repository;

import com.microslop.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    /** Comprueba si un usuario ya ha votado a un proyecto concreto. */
    boolean existsByUserUsernameAndProjectId(String username, Long projectId);

    /** Total de votos recibidos por un proyecto. */
    long countByProjectId(Long projectId);

    /** Total de votos emitidos por un usuario en toda una competición. */
    @Query("""
        SELECT COUNT(v) FROM Vote v
        WHERE v.user.id = :username
        AND v.project.competition.id = :competitionId
        """)
    long countByUsuarioEnCompeticion(@Param("username") String username,
                                     @Param("competitionId") Long competitionId);
}
