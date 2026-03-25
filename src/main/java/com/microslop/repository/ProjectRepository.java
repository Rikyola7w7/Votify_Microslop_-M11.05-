package com.microslop.repository;

import com.microslop.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    //Buscar proyecto por ID
    List<Project> findByCompetitionId(Long competitionId);

    //Proyectos de una competición ordenados en orden descendente por número de votos
    @Query("""
        SELECT p FROM Project p
        LEFT JOIN p.votes v
        WHERE p.competition.id = :competitionId
        GROUP BY p
        ORDER BY COUNT(v) DESC
        """)
    List<Project> findRankingByCompetition(@Param("competitionId") Long competitionId);
}
