package com.microslop.factory;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import org.springframework.stereotype.Component;

/**
 * Factory for creating Project instances with validation.
 */
@Component
public class ProjectFactory {

    public Project create(String name,
                        String description,
                        Competition competition) {
        validateNotEmpty(name, "Project name cannot be empty.");
        if (competition == null) {
            throw new IllegalArgumentException("Project must belong to a competition.");
        }

        return new Project(name, description, competition);
    }

    private void validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
