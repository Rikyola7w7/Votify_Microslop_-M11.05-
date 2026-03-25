package com.microslop.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
public class Project {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(length = 2000, name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    public Project() {}

    public Project(String name, String description, Competition competition) {
        this.name       = name;
        this.description  = description;
        this.competition  = competition;
    }

    public void addVoto(Vote voto) {
        votes.add(voto);
        voto.setProject(this);
    }

    public void removeVoto(Vote voto) {
        votes.remove(voto);
        voto.setProject(null);
    }

    public long getTotalVotos() {
        return votes.size();
    }

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getNombre()                    { return name; }
    public void setNombre(String name)         { this.name = name; }

    public String getDescripcion()               { return description; }
    public void setDescripcion(String description)         { this.description = description; }

    public Competition getCompeticion()          { return competition; }
    public void setCompeticion(Competition competition)    { this.competition = competition; }

    public List<Vote> getVotos()                 { return votes; }
    public void setVotos(List<Vote> votes)       { this.votes = votes; }

    @Override
    public String toString() {
        return "Project{id=" + id + ", name='" + name + "', votes=" + getTotalVotos() + "}";
    }
}
