package com.microslop.service.impl;

import com.microslop.entity.*;
import com.microslop.repository.*;
import com.microslop.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PendingProjectSubmissionServiceImpl implements PendingProjectSubmissionService {

    private final PendingProjectSubmissionRepository repository;
    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProjectService projectService;
    private final NotificationService notificationService;

    public PendingProjectSubmissionServiceImpl(PendingProjectSubmissionRepository repository,
                                                CompetitionRepository competitionRepository,
                                                UserRepository userRepository,
                                                CategoryRepository categoryRepository,
                                                ProjectService projectService,
                                                NotificationService notificationService) {
        this.repository = repository;
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.projectService = projectService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public PendingProjectSubmission createSubmission(String projectName, String description,
                                                      Long competitionId, String submitterUsername,
                                                      String categoryIds) {
        Competition competition = competitionRepository.findById(competitionId)
            .orElseThrow(() -> new RuntimeException("Competition not found"));
        User submitter = userRepository.findByUsernameIgnoreCase(submitterUsername)
            .orElseThrow(() -> new RuntimeException("User not found"));

        PendingProjectSubmission submission = new PendingProjectSubmission(
            projectName, description, competition, submitter, categoryIds
        );
        return repository.save(submission);
    }

    @Override
    public List<PendingProjectSubmission> getSubmissionsByCompetition(Long competitionId) {
        return repository.findByCompetitionId(competitionId);
    }

    @Override
    @Transactional
    public void acceptSubmission(Long submissionId) {
        PendingProjectSubmission submission = repository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Pending submission not found"));

        Project project = new Project(submission.getProjectName(), submission.getDescription(), submission.getCompetition());
        project.addParticipant(submission.getSubmitter());

        if (submission.getCategoryIds() != null && !submission.getCategoryIds().isEmpty()) {
            for (String idStr : submission.getCategoryIds().split(",")) {
                try {
                    Long catId = Long.parseLong(idStr.trim());
                    categoryRepository.findById(catId).ifPresent(project::addCategory);
                } catch (NumberFormatException ignored) {}
            }
        }

        projectService.save(project);

        notifyUser(submission.getSubmitter(),
            "Project Accepted",
            "Your project \"" + submission.getProjectName() + "\" has been accepted to \"" + submission.getCompetition().getName() + "\"!",
            "PROJECT_ACCEPTED"
        );

        repository.delete(submission);
    }

    @Override
    @Transactional
    public void declineSubmission(Long submissionId) {
        PendingProjectSubmission submission = repository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Pending submission not found"));

        notifyUser(submission.getSubmitter(),
            "Project Declined",
            "Your project \"" + submission.getProjectName() + "\" has been declined for \"" + submission.getCompetition().getName() + "\".",
            "PROJECT_DECLINED"
        );

        repository.delete(submission);
    }

    private void notifyUser(User user, String title, String message, String type) {
        try {
            if (notificationService != null) {
                notificationService.createNotification(user, title, message, type);
            }
        } catch (Exception ignored) {}
    }
}
