package com.microslop.repository;

import com.microslop.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    /** Comprueba si un usuario ya ha votado a un proyecto concreto. */
    boolean existsByUsuarioIdAndProyectoId(Long usuarioId, Long proyectoId);

    /** Total de votos recibidos por un proyecto. */
    long countByProyectoId(Long proyectoId);

    /** Total de votos emitidos por un usuario en toda una competición. */
    @Query("""
        SELECT COUNT(v) FROM Voto v
        WHERE v.usuario.id = :usuarioId
        AND v.proyecto.competicion.id = :competicionId
        """)
    long countByUsuarioEnCompeticion(@Param("usuarioId") Long usuarioId,
                                     @Param("competicionId") Long competicionId);
}