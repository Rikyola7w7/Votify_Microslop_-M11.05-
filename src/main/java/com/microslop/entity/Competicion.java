package com.microslop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competicion")
public class Competicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private boolean activa = true;

    @OneToMany(mappedBy = "competicion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Proyecto> proyectos = new ArrayList<>();

    // ── Constructores ────────────────────────────────────────────────────────

    public Competicion() {}

    public Competicion(String nombre, String descripcion,
                       LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin    = fechaFin;
    }

    // ── Helpers de relación ──────────────────────────────────────────────────

    public void addProyecto(Proyecto proyecto) {
        proyectos.add(proyecto);
        proyecto.setCompeticion(this);
    }

    public void removeProyecto(Proyecto proyecto) {
        proyectos.remove(proyecto);
        proyecto.setCompeticion(null);
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public String getNombre()                   { return nombre; }
    public void setNombre(String nombre)        { this.nombre = nombre; }

    public String getDescripcion()              { return descripcion; }
    public void setDescripcion(String d)        { this.descripcion = d; }

    public LocalDateTime getFechaInicio()       { return fechaInicio; }
    public void setFechaInicio(LocalDateTime f) { this.fechaInicio = f; }

    public LocalDateTime getFechaFin()          { return fechaFin; }
    public void setFechaFin(LocalDateTime f)    { this.fechaFin = f; }

    public boolean isActiva()                   { return activa; }
    public void setActiva(boolean activa)       { this.activa = activa; }

    public List<Proyecto> getProyectos()        { return proyectos; }
    public void setProyectos(List<Proyecto> p)  { this.proyectos = p; }

    @Override
    public String toString() {
        return "Competicion{id=" + id + ", nombre='" + nombre + "', activa=" + activa + "}";
    }
}