package com.microslop.repository;

import com.microslop.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    //Buscar proyecto por ID
    List<Proyecto> findByCompeticionId(Long competicionId);

    //Proyectos de una competición ordenados en orden descendente por número de votos
    @Query("""
        SELECT p FROM Proyecto p
        LEFT JOIN p.votos v
        WHERE p.competicion.id = :competicionId
        GROUP BY p
        ORDER BY COUNT(v) DESC
        """)
    List<Proyecto> findRankingByCompeticion(@Param("competicionId") Long competicionId);
}