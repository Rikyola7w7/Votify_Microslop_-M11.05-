package com.microslop.repository;

import com.microslop.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    //Todas las competiciones activas
    List<Competition> findByActiveTrue();

    List<Competition> findByActiveFalse();

    //Buscar competición por nombre
    Optional<Competition> findByNameIgnoreCase(String name);

    //Competiciones activas con sus proyectos precargados
    @Query("SELECT DISTINCT c FROM Competition c LEFT JOIN FETCH c.projects WHERE c.active = true")
    List<Competition> findActivasConProyectos();
}
