package com.microslop.service.impl;

import com.microslop.entity.*;
import com.microslop.repository.*;
import com.microslop.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class PendingProjectSubmissionServiceImpl implements PendingProjectSubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(PendingProjectSubmissionServiceImpl.class);

    private final PendingProjectSubmissionRepository repository;
    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProjectService projectService;
    private final NotificationService notificationService;
    private final InvitationService invitationService;

    public PendingProjectSubmissionServiceImpl(PendingProjectSubmissionRepository repository,
                                                CompetitionRepository competitionRepository,
                                                UserRepository userRepository,
                                                CategoryRepository categoryRepository,
                                                ProjectService projectService,
                                                NotificationService notificationService,
                                                InvitationService invitationService) {
        this.repository = repository;
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.projectService = projectService;
        this.notificationService = notificationService;
        this.invitationService = invitationService;
    }

    @Override
    @Transactional
    public PendingProjectSubmission createSubmission(String projectName, String description,
                                                      Long competitionId, String submitterUsername,
                                                      String categoryIds) {
        return createSubmission(projectName, description, competitionId, submitterUsername, categoryIds, "");
    }

    @Override
    @Transactional
    public PendingProjectSubmission createSubmission(String projectName, String description,
                                                      Long competitionId, String submitterUsername,
                                                      String categoryIds, String invitedParticipantIds) {
        Competition competition = competitionRepository.findById(competitionId)
            .orElseThrow(() -> new RuntimeException("Competition not found"));
        User submitter = userRepository.findByUsernameIgnoreCase(submitterUsername)
            .orElseThrow(() -> new RuntimeException("User not found"));

        PendingProjectSubmission submission = new PendingProjectSubmission(
            projectName, description, competition, submitter, categoryIds, invitedParticipantIds
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

        // Create invitations for invited participants
        if (submission.getInvitedParticipantIds() != null && !submission.getInvitedParticipantIds().isEmpty()) {
            logger.info("Processing invitations for project: {}", project.getId());
            for (String idStr : submission.getInvitedParticipantIds().split(",")) {
                try {
                    Long userId = Long.parseLong(idStr.trim());
                    logger.debug("Processing invitation for user ID: {}", userId);
                    
                    userRepository.findById(userId).ifPresentOrElse(
                        invitedUser -> {
                            logger.info("Found invited user: {}", invitedUser.getUsername());
                            try {
                                // Create invitation first
                                Invitation invitation = invitationService.createInvitation(
                                    invitedUser,
                                    project.getId(),
                                    project.getName(),
                                    submission.getCompetition().getId(),
                                    submission.getSubmitter()
                                );
                                logger.info("Created invitation {} for user {}", invitation.getId(), invitedUser.getUsername());
                                
                                // Send notification with invitation reference
                                Notification notification = notificationService.createNotification(
                                    invitedUser,
                                    "Project Invitation",
                                    submission.getSubmitter().getUsername() + " invited you to join \"" + submission.getProjectName() + "\" in \"" + submission.getCompetition().getName() + "\".",
                                    "PROJECT_INVITATION",
                                    invitation.getId()
                                );
                                logger.info("Sent notification {} to user {} for invitation {}", notification.getId(), invitedUser.getUsername(), invitation.getId());
                            } catch (Exception e) {
                                logger.error("Error sending invitation to user {}: {}", invitedUser.getUsername(), e.getMessage(), e);
                            }
                        },
                        () -> logger.warn("User with ID {} not found", userId)
                    );
                } catch (NumberFormatException e) {
                    logger.warn("Invalid user ID format: {}", idStr, e);
                }
            }
        }

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
