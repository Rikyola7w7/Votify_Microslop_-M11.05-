package com.microslop.factory;

import com.microslop.entity.Project;
import com.microslop.entity.ProjectComment;
import com.microslop.entity.User;
import org.springframework.stereotype.Component;

/**
 * Factory for creating ProjectComment instances with validation.
 */
@Component
public class ProjectCommentFactory {

    public ProjectComment create(Project project, User user, String commentText, com.microslop.entity.Category category) {
        if (project == null) {
            throw new IllegalArgumentException("Comment must be associated with a project.");
        }
        if (user == null) {
            throw new IllegalArgumentException("Comment must be associated with a user.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Comment must be associated with a category.");
        }
        validateNotEmpty(commentText, "Comment text cannot be empty.");

        return new ProjectComment(project, user, commentText, category);
    }

    private void validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
