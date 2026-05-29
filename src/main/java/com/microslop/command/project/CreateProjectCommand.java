package com.microslop.command.project;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.CompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to create a new project within a competition.
 * This command encapsulates the project creation logic and supports undo/redo operations.
 *
 * @see AbstractCommand
 */
public class CreateProjectCommand extends AbstractCommand<Project> {

    private static final Logger log = LoggerFactory.getLogger(CreateProjectCommand.class);

    private final String projectName;
    private final String projectDescription;
    private final Long competitionId;

    private final ProjectRepository projectRepository;
    private final CompetitionRepository competitionRepository;

    // Store the created project for undo operation
    private Project createdProject;

    /**
     * Creates a new CreateProjectCommand.
     *
     * @param projectName the name of the project
     * @param projectDescription the description of the project
     * @param competitionId the ID of the competition this project belongs to
     * @param projectRepository the repository to persist projects
     * @param competitionRepository the repository to retrieve competition information
     */
    public CreateProjectCommand(String projectName, String projectDescription, Long competitionId,
                               ProjectRepository projectRepository, CompetitionRepository competitionRepository) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.competitionId = competitionId;
        this.projectRepository = projectRepository;
        this.competitionRepository = competitionRepository;
    }

    /**
     * {@inheritDoc}
     * Executes the project creation with all necessary validations.
     */
    @Override
    protected Project executeCommand() throws Exception {
        log.debug("Creating project: name={}, competitionId={}", projectName, competitionId);

        // Validate input
        if (projectName == null || projectName.isBlank()) {
            throw new IllegalArgumentException("Project name cannot be null or blank");
        }

        if (competitionId == null || competitionId <= 0) {
            throw new IllegalArgumentException("Competition ID must be a positive number");
        }

        // Retrieve and validate competition
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        // Create the project
        createdProject = new Project(projectName, projectDescription, competition);

        // Save to repository
        createdProject = projectRepository.save(createdProject);
        lastResult = createdProject;

        log.info("Project successfully created - ID: {}, Name: {}, Competition: {}", 
                 createdProject.getId(), createdProject.getName(), competitionId);

        return createdProject;
    }

    /**
     * {@inheritDoc}
     * Undoes the project creation by deleting the created project from the repository.
     */
    @Override
    public void undo() throws Exception {
        if (createdProject == null || createdProject.getId() == null) {
            throw new IllegalStateException("Cannot undo: Project was not properly created or ID is missing");
        }

        log.debug("Undoing project creation - Project ID: {}", createdProject.getId());
        projectRepository.deleteById(createdProject.getId());
        log.info("Project successfully undone - Project ID: {}", createdProject.getId());
    }

    /**
     * {@inheritDoc}
     * Redoes the project creation by recreating and persisting the project.
     */
    @Override
    public Project redo() throws Exception {
        if (createdProject == null) {
            throw new IllegalStateException("Cannot redo: Project information was not preserved");
        }

        log.debug("Redoing project creation - Name: {}", createdProject.getName());

        // Retrieve the competition again to ensure it still exists
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        // Recreate the project with the same data
        Project redoneProject = new Project(
            createdProject.getName(),
            createdProject.getDescription(),
            competition
        );

        redoneProject = projectRepository.save(redoneProject);
        createdProject.setId(redoneProject.getId()); // Update ID for future undo/redo cycles
        lastResult = redoneProject;

        log.info("Project successfully redone - Project ID: {}", redoneProject.getId());
        return redoneProject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("Create project '%s' in competition %d", projectName, competitionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }
}
