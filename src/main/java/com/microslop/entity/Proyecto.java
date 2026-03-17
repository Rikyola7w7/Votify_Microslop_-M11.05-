package com.microslop.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proyecto")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 2000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competicion_id", nullable = false)
    private Competicion competicion;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voto> votos = new ArrayList<>();

    // ── Constructores ────────────────────────────────────────────────────────

    public Proyecto() {}

    public Proyecto(String nombre, String descripcion, Competicion competicion) {
        this.nombre       = nombre;
        this.descripcion  = descripcion;
        this.competicion  = competicion;
    }

    // ── Helpers de relación ──────────────────────────────────────────────────

    public void addVoto(Voto voto) {
        votos.add(voto);
        voto.setProyecto(this);
    }

    public void removeVoto(Voto voto) {
        votos.remove(voto);
        voto.setProyecto(null);
    }

    public long getTotalVotos() {
        return votos.size();
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getNombre()                    { return nombre; }
    public void setNombre(String nombre)         { this.nombre = nombre; }

    public String getDescripcion()               { return descripcion; }
    public void setDescripcion(String d)         { this.descripcion = d; }

    public Competicion getCompeticion()          { return competicion; }
    public void setCompeticion(Competicion c)    { this.competicion = c; }

    public List<Voto> getVotos()                 { return votos; }
    public void setVotos(List<Voto> votos)       { this.votos = votos; }

    @Override
    public String toString() {
        return "Proyecto{id=" + id + ", nombre='" + nombre + "', votos=" + getTotalVotos() + "}";
    }
}