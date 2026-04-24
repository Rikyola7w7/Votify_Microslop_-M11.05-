package com.microslop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "weight")
    private Integer weight;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    public Category() {}

    public Category(String name, Integer weight, Competition competition) {
        this.name = name;
        this.weight = weight;
        this.competition = competition;
    }

    public Long getId()                             { return id; }
    public void setId(Long id)                      { this.id = id; }

    public String getName()                         { return name; }
    public void setName(String name)                { this.name = name; }

    public Integer getWeight()                      { return weight; }
    public void setWeight(Integer weight)           { this.weight = weight; }

    public Competition getCompetition()             { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "', weight=" + weight + "}";
    }
}
