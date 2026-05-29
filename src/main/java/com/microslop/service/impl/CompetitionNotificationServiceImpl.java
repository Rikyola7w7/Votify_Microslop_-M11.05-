package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.entity.Voter;
import com.microslop.factory.notification.CompetitionClosedNotificationCreator;
import com.microslop.factory.notification.CompetitionClosingSoonNotificationCreator;
import com.microslop.factory.notification.CompetitionOpenedNotificationCreator;
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
    private final CompetitionOpenedNotificationCreator competitionOpenedNotificationCreator;
    private final CompetitionClosingSoonNotificationCreator competitionClosingSoonNotificationCreator;
    private final CompetitionClosedNotificationCreator competitionClosedNotificationCreator;

    public CompetitionNotificationServiceImpl(NotificationService notificationService,
                                              VoterRepository voterRepository,
                                              CompetitionOpenedNotificationCreator competitionOpenedNotificationCreator,
                                              CompetitionClosingSoonNotificationCreator competitionClosingSoonNotificationCreator,
                                              CompetitionClosedNotificationCreator competitionClosedNotificationCreator) {
        this.notificationService = notificationService;
        this.voterRepository = voterRepository;
        this.competitionOpenedNotificationCreator = competitionOpenedNotificationCreator;
        this.competitionClosingSoonNotificationCreator = competitionClosingSoonNotificationCreator;
        this.competitionClosedNotificationCreator = competitionClosedNotificationCreator;
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
                notificationService.saveAndPublish(
                    competitionOpenedNotificationCreator.createWithCompetition(
                        user,
                        "Competition " + competition.getName() + " is now open",
                        "Voting for competition '" + competition.getName() + "' has started. You can now cast your votes.",
                        competition
                    )
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
                notificationService.saveAndPublish(
                    competitionClosingSoonNotificationCreator.createWithCompetition(
                        user,
                        "Competition " + competition.getName() + " is closing soon",
                        "Voting for competition '" + competition.getName() + "' will close in approximately 1 hour. Make sure to cast your votes before the deadline.",
                        competition
                    )
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
                notificationService.saveAndPublish(
                    competitionClosedNotificationCreator.createWithCompetition(
                        user,
                        "Competition " + competition.getName() + " is closed",
                        "Voting for competition '" + competition.getName() + "' has ended. Thank you for participating!",
                        competition
                    )
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
