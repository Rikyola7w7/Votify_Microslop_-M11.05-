package com.microslop.builder;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;

public class ProjectBuilder {
    private String name;
    private String description;
    private Competition competition;

    public static ProjectBuilder builder() {
        return new ProjectBuilder();
    }

    public ProjectBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProjectBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProjectBuilder competition(Competition competition) {
        this.competition = competition;
        return this;
    }

    public Project build() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        if (competition == null) {
            throw new IllegalArgumentException("Project must belong to a competition.");
        }
        return new Project(name, description, competition);
    }
}
