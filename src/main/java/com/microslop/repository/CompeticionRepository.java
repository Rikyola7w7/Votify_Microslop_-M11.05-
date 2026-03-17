package com.microslop.repository;

import com.microslop.entity.Competicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompeticionRepository extends JpaRepository<Competicion, Long> {

    //Todas las competiciones activas
    List<Competicion> findByActivaTrue();

    //Buscar competición por nombre
    Optional<Competicion> findByNombreIgnoreCase(String nombre);

    //Competiciones activas con sus proyectos precargados
    @Query("SELECT DISTINCT c FROM Competicion c LEFT JOIN FETCH c.proyectos WHERE c.activa = true")
    List<Competicion> findActivasConProyectos();
}