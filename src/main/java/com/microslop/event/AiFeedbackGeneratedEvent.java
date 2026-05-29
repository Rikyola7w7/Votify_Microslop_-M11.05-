package com.microslop.event;

import com.microslop.dto.AiFeedbackResult;

/**
 * Event fired when AI feedback is successfully generated for a project.
 * Used by observers to react to feedback generation (e.g., logging, analytics).
 */
public class AiFeedbackGeneratedEvent {

    private final Long projectId;
    private final String projectName;
    private final String username;
    private final AiFeedbackResult result;

    public AiFeedbackGeneratedEvent(Long projectId, String projectName, String username, AiFeedbackResult result) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.username = username;
        this.result = result;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getUsername() {
        return username;
    }

    public AiFeedbackResult getResult() {
        return result;
    }
}
