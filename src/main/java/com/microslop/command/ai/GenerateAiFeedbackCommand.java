package com.microslop.command.ai;

import com.microslop.command.AbstractCommand;
import com.microslop.dto.AiFeedbackResult;
import com.microslop.service.AiFeedbackService;

/**
 * Command that encapsulates AI feedback generation for a project.
 * Follows the Command Design Pattern.
 */
public class GenerateAiFeedbackCommand extends AbstractCommand<AiFeedbackResult> {

    private final Long projectId;
    private final AiFeedbackService aiFeedbackService;

    public GenerateAiFeedbackCommand(Long projectId, AiFeedbackService aiFeedbackService) {
        this.projectId = projectId;
        this.aiFeedbackService = aiFeedbackService;
    }

    @Override
    protected AiFeedbackResult executeCommand() throws Exception {
        return aiFeedbackService.generateFeedbackForProject(projectId);
    }

    @Override
    public void undo() throws Exception {
        throw new UnsupportedOperationException("GenerateAiFeedbackCommand does not support undo");
    }

    @Override
    public AiFeedbackResult redo() throws Exception {
        return executeCommand();
    }

    @Override
    public String getDescription() {
        return String.format("Generate AI feedback for project %d", projectId);
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
