package com.microslop.service;

import com.microslop.entity.Proyecto;
import com.microslop.repository.ProyectoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    // ── Escritura ────────────────────────────────────────────────────────────

    public Proyecto guardar(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    public void eliminar(Long id) {
        proyectoRepository.deleteById(id);
    }

    // ── Lectura ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<Proyecto> obtenerPorId(Long id) {
        return proyectoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Proyecto obtenerPorIdOFallar(Long id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Proyecto> listarPorCompeticion(Long competicionId) {
        return proyectoRepository.findByCompeticionId(competicionId);
    }

    
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerRanking(Long competicionId) {
        return proyectoRepository.findRankingByCompeticion(competicionId);
    }
}