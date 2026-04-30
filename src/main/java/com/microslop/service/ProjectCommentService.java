package com.microslop.service;

import com.microslop.entity.ProjectComment;
import java.util.List;

public interface ProjectCommentService {

    /**
     * Save a new comment for a project.
     */
    void saveComment(Long projectId, String username, String commentText, Long categoryId);

    /**
     * Get all comments for a project, ordered by creation date (newest first).
     */
    List<ProjectComment> getCommentsByProject(Long projectId);

    /**
     * Get all comments by a user, ordered by creation date (newest first).
     */
    List<ProjectComment> getCommentsByUser(String username);

    /**
     * Count comments for a project.
     */
    long countCommentsByProject(Long projectId);

    /**
     * Delete a comment by ID.
     */
    void deleteComment(Long commentId);
}
