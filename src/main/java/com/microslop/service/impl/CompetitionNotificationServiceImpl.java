package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.entity.Voter;
import com.microslop.enums.NotificationType;
import com.microslop.repository.VoterRepository;
import com.microslop.service.CompetitionNotificationService;
import com.microslop.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for sending notifications to all voters in a competition
 */
@Service
public class CompetitionNotificationServiceImpl implements CompetitionNotificationService {

    private static final Logger log = LoggerFactory.getLogger(CompetitionNotificationServiceImpl.class);

    private final NotificationService notificationService;
    private final VoterRepository voterRepository;

    public CompetitionNotificationServiceImpl(NotificationService notificationService,
                                           VoterRepository voterRepository) {
        this.notificationService = notificationService;
        this.voterRepository = voterRepository;
    }

    @Override
    public void notifyCompetitionOpened(Competition competition) {
        if (competition == null) {
            log.warn("Cannot notify: competition is null");
            return;
        }

        try {
            // Get all registered voters for this competition
            List<Voter> voters = voterRepository.findByCompetition(competition);
            if (voters.isEmpty()) {
                log.info("No voters registered for competition {}, skipping notification", competition.getId());
                return;
            }

            // Get distinct users from voters
            var distinctUsers = voters.stream()
                .map(Voter::getUser)
                .distinct()
                .collect(Collectors.toList());

            // Send notification to each voter
            for (var user : distinctUsers) {
                notificationService.createNotification(
                    user,
                    "Competition " + competition.getName() + " is now open",
                    "Voting for competition '" + competition.getName() + "' has started. You can now cast your votes.",
                    NotificationType.COMPETITION_OPENED.getCode(),
                    competition
                );
            }

            log.info("Sent COMPETITION_OPENED notification to {} voters for competition {}", 
                distinctUsers.size(), competition.getId());

        } catch (Exception e) {
            log.error("Error notifying competition opened for competition {}: {}", 
                competition.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void notifyCompetitionClosingSoon(Competition competition) {
        if (competition == null) {
            log.warn("Cannot notify: competition is null");
            return;
        }

        try {
            // Get all registered voters for this competition
            List<Voter> voters = voterRepository.findByCompetition(competition);
            if (voters.isEmpty()) {
                log.info("No voters registered for competition {}, skipping notification", competition.getId());
                return;
            }

            // Get distinct users from voters
            var distinctUsers = voters.stream()
                .map(Voter::getUser)
                .distinct()
                .collect(Collectors.toList());

            // Send notification to each voter
            for (var user : distinctUsers) {
                notificationService.createNotification(
                    user,
                    "Competition " + competition.getName() + " is closing soon",
                    "Voting for competition '" + competition.getName() + "' will close in approximately 1 hour. Make sure to cast your votes before the deadline.",
                    NotificationType.COMPETITION_CLOSING_SOON.getCode(),
                    competition
                );
            }

            log.info("Sent COMPETITION_CLOSING_SOON notification to {} voters for competition {}", 
                distinctUsers.size(), competition.getId());

        } catch (Exception e) {
            log.error("Error notifying competition closing soon for competition {}: {}", 
                competition.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void notifyCompetitionClosed(Competition competition) {
        if (competition == null) {
            log.warn("Cannot notify: competition is null");
            return;
        }

        try {
            // Get all registered voters for this competition
            List<Voter> voters = voterRepository.findByCompetition(competition);
            if (voters.isEmpty()) {
                log.info("No voters registered for competition {}, skipping notification", competition.getId());
                return;
            }

            // Get distinct users from voters
            var distinctUsers = voters.stream()
                .map(Voter::getUser)
                .distinct()
                .collect(Collectors.toList());

            // Send notification to each voter
            for (var user : distinctUsers) {
                notificationService.createNotification(
                    user,
                    "Competition " + competition.getName() + " is closed",
                    "Voting for competition '" + competition.getName() + "' has ended. Thank you for participating!",
                    NotificationType.COMPETITION_CLOSED.getCode(),
                    competition
                );
            }

            log.info("Sent COMPETITION_CLOSED notification to {} voters for competition {}", 
                distinctUsers.size(), competition.getId());

        } catch (Exception e) {
            log.error("Error notifying competition closed for competition {}: {}", 
                competition.getId(), e.getMessage(), e);
        }
    }
}
