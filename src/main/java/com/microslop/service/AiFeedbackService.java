package com.microslop.service;

import com.microslop.dto.AiFeedbackResult;

/**
 * Service for generating and retrieving AI feedback for projects.
 */
public interface AiFeedbackService {

    /**
     * Generates AI feedback for a project by calling the Gemini API.
     * Persists the result to the database.
     *
     * @param projectId the project ID
     * @return the generated feedback result
     */
    AiFeedbackResult generateFeedbackForProject(Long projectId);

    /**
     * Retrieves the most recently generated feedback for a project, if any.
     *
     * @param projectId the project ID
     * @return the feedback result, or null if none exists
     */
    AiFeedbackResult getExistingFeedbackForProject(Long projectId);

    /**
     * Checks if feedback has already been generated for a project.
     *
     * @param projectId the project ID
     * @return true if feedback exists
     */
    boolean hasFeedbackForProject(Long projectId);
}
