package com.microslop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competition")
public class Competition {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(length = 1000, name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startdate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(nullable = false, name = "active")
    private boolean active = true;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    public Competition() {}

    public Competition(String nombre, String descripcion,
                       LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.name      = nombre;
        this.description = descripcion;
        this.startdate = fechaInicio;
        this.endDate = fechaFin;
    }

    public void addProyecto(Project proyecto) {
        projects.add(proyecto);
        proyecto.setCompeticion(this);
    }

    public void removeProyecto(Project proyecto) {
        projects.remove(proyecto);
        proyecto.setCompeticion(null);
    }

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public String getNombre()                   { return name; }
    public void setNombre(String nombre)        { this.name = nombre; }
    
    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }

    public String getDescripcion()              { return description; }
    public void setDescripcion(String d)        { this.description = d; }
    
    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }

    public LocalDateTime getFechaInicio()       { return startdate; }
    public void setFechaInicio(LocalDateTime f) { this.startdate = f; }
    
    public LocalDateTime getStartDate()         { return startdate; }
    public void setStartDate(LocalDateTime f)   { this.startdate = f; }

    public LocalDateTime getFechaFin()          { return endDate; }
    public void setFechaFin(LocalDateTime f)    { this.endDate = f; }
    
    public LocalDateTime getEndDate()           { return endDate; }
    public void setEndDate(LocalDateTime f)     { this.endDate = f; }

    public boolean isActiva()                   { return active; }
    public void setActiva(boolean activa)       { this.active = activa; }
    
    public boolean isActive()                   { return active; }
    public void setActive(boolean active)       { this.active = active; }

    public List<Project> getProyectos()        { return projects; }
    public void setProyectos(List<Project> p)  { this.projects = p; }
    
    public List<Project> getProjects()         { return projects; }
    public void setProjects(List<Project> p)   { this.projects = p; }

    @Override
    public String toString() {
        return "Competition{id=" + id + ", name='" + name + "', active=" + active + "}";
    }
}
