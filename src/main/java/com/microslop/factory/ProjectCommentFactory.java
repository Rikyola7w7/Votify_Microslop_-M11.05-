package com.microslop.factory;

import com.microslop.entity.Project;
import com.microslop.entity.ProjectComment;
import org.springframework.stereotype.Component;

/**
 * Factory for creating ProjectComment instances with validation.
 */
@Component
public class ProjectCommentFactory {

    public ProjectComment create(Project project, String username, String commentText) {
        if (project == null) {
            throw new IllegalArgumentException("Comment must be associated with a project.");
        }
        validateNotEmpty(username, "Username cannot be empty.");
        validateNotEmpty(commentText, "Comment text cannot be empty.");

        return new ProjectComment(project, username, commentText);
    }

    private void validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
