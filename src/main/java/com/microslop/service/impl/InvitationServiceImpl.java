package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.entity.Invitation;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.exception.EntityNotFoundException;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.InvitationRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.service.InvitationService;
import com.microslop.service.NotificationService;
import com.microslop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InvitationServiceImpl implements InvitationService {

    private static final Logger log = LoggerFactory.getLogger(InvitationServiceImpl.class);

    private final InvitationRepository invitationRepository;
    private final ProjectRepository projectRepository;
    private final CompetitionRepository competitionRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public InvitationServiceImpl(InvitationRepository invitationRepository,
                                ProjectRepository projectRepository,
                                CompetitionRepository competitionRepository,
                                UserService userService,
                                NotificationService notificationService) {
        this.invitationRepository = invitationRepository;
        this.projectRepository = projectRepository;
        this.competitionRepository = competitionRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Invitation createInvitation(User invitedUser, Long projectId, String projectName, Long competitionId, User invitedBy) {
        Competition competition = competitionRepository.findById(competitionId)
            .orElseThrow(() -> new EntityNotFoundException("Competition", competitionId));

        Invitation invitation = new Invitation(invitedUser, projectId, projectName, competition, invitedBy);
        Invitation saved = invitationRepository.save(invitation);
        log.info("Invitation created: user {} invited by {} to project {}", invitedUser.getUsername(), invitedBy.getUsername(), projectName);
        return saved;
    }

    @Override
    public List<Invitation> getPendingInvitationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return invitationRepository.findPendingInvitationsForUser(currentUser);
    }

    @Override
    public List<Invitation> getInvitationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return invitationRepository.findByUserOrderByCreationDateDesc(currentUser);
    }

    @Override
    @Transactional
    public Invitation acceptInvitation(Long invitationId) {
        User currentUser = userService.getCurrentUser();
        Invitation invitation = invitationRepository.findByIdAndUser(invitationId, currentUser)
            .orElseThrow(() -> new EntityNotFoundException("Invitation", invitationId));

        if (!invitation.isPending()) {
            throw new IllegalStateException("Invitation is not pending");
        }

        Optional<Project> projectOpt = projectRepository.findById(invitation.getProjectId());
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.addParticipant(currentUser);
            projectRepository.save(project);
        }

        invitation.setStatus(Invitation.InvitationStatus.ACCEPTED);
        Invitation updatedInvitation = invitationRepository.save(invitation);
        log.info("Invitation {} accepted by user {}", invitationId, currentUser.getUsername());

        try {
            notificationService.createNotification(
                invitation.getInvitedBy(),
                "Invitation Accepted",
                currentUser.getUsername() + " accepted your invitation to join \"" + invitation.getProjectName() + "\"",
                "INVITATION_ACCEPTED"
            );
        } catch (Exception e) {
            log.warn("Failed to send notification for accepted invitation {}: {}", invitationId, e.getMessage());
        }

        return updatedInvitation;
    }

    @Override
    @Transactional
    public Invitation refuseInvitation(Long invitationId) {
        User currentUser = userService.getCurrentUser();
        Invitation invitation = invitationRepository.findByIdAndUser(invitationId, currentUser)
            .orElseThrow(() -> new EntityNotFoundException("Invitation", invitationId));

        if (!invitation.isPending()) {
            throw new IllegalStateException("Invitation is not pending");
        }

        invitation.setStatus(Invitation.InvitationStatus.REFUSED);
        Invitation updatedInvitation = invitationRepository.save(invitation);
        log.info("Invitation {} refused by user {}", invitationId, currentUser.getUsername());

        try {
            notificationService.createNotification(
                invitation.getInvitedBy(),
                "Invitation Refused",
                currentUser.getUsername() + " refused your invitation to join \"" + invitation.getProjectName() + "\"",
                "INVITATION_REFUSED"
            );
        } catch (Exception e) {
            log.warn("Failed to send notification for refused invitation {}: {}", invitationId, e.getMessage());
        }

        return updatedInvitation;
    }

    @Override
    public Optional<Invitation> getInvitation(Long invitationId) {
        return invitationRepository.findById(invitationId);
    }

    @Override
    public List<Invitation> getInvitationsByProjectId(Long projectId) {
        return invitationRepository.findByProjectId(projectId);
    }
}
