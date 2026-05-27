package com.microslop.scheduled;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.enums.NotificationType;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.UserRepository;
import com.microslop.service.CompetitionNotificationService;
import com.microslop.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled task to check for competitions closing soon and send notifications to voters
 * Also checks for competitions that have ended and notifies admin
 * Runs every 5 minutes to find competitions that will close within 1 hour
 */
@Component
public class CompetitionNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(CompetitionNotificationScheduler.class);

    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final CompetitionNotificationService competitionNotificationService;
    private final NotificationService notificationService;

    public CompetitionNotificationScheduler(CompetitionRepository competitionRepository,
                                           UserRepository userRepository,
                                           CompetitionNotificationService competitionNotificationService,
                                           NotificationService notificationService) {
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.competitionNotificationService = competitionNotificationService;
        this.notificationService = notificationService;
    }

    /**
     * Check for competitions closing soon and send notifications
     * Runs every 5 minutes (300,000 milliseconds)
     */
    @Scheduled(fixedRate = 300000)
    public void checkAndNotifyCompetitionsClosingSoon() {
        try {
            log.debug("Starting scheduled check for competitions closing soon");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourFromNow = now.plusHours(1);

            // Find all competitions in VOTING_OPEN status
            List<Competition> openCompetitions = competitionRepository.findByStatus(CompetitionStatus.VOTING_OPEN);

            if (openCompetitions.isEmpty()) {
                log.debug("No competitions in VOTING_OPEN status");
                return;
            }

            // Filter competitions closing within 1 hour
            for (Competition competition : openCompetitions) {
                if (competition.getEndDate() != null &&
                    competition.getEndDate().isAfter(now) &&
                    competition.getEndDate().isBefore(oneHourFromNow)) {

                    // Check if notification was already sent for this competition
                    // For now, we'll send it every time (can be enhanced with a flag later)
                    log.info("Competition {} is closing within 1 hour, sending notifications", competition.getId());
                    competitionNotificationService.notifyCompetitionClosingSoon(competition);
                }
            }

        } catch (Exception e) {
            log.error("Error in scheduled competition notification check: {}", e.getMessage(), e);
        }
    }

    /**
     * Check for competitions that have ended and notify admin
     * Runs every 5 minutes (300,000 milliseconds)
     */
    @Scheduled(fixedRate = 300000)
    public void checkAndNotifyCompetitionEndTime() {
        try {
            log.debug("Starting scheduled check for competitions that have ended");

            LocalDateTime now = LocalDateTime.now();

            // Find all competitions in VOTING_OPEN status that have passed their end date
            List<Competition> openCompetitions = competitionRepository.findByStatus(CompetitionStatus.VOTING_OPEN);

            for (Competition competition : openCompetitions) {
                if (competition.getEndDate() != null && competition.getEndDate().isBefore(now)) {
                    // Check if notification was already sent for this competition
                    if (competition.isEndNotificationSent()) {
                        log.debug("END_TIME_COMPETITION notification already sent for competition {}, skipping", 
                                competition.getId());
                        continue;
                    }

                    log.info("Competition {} has ended (endDate: {}), notifying admin", 
                            competition.getId(), competition.getEndDate());
                    
                    // Update competition status to CONCLUDED
                    competition.conclude();

                    // Send END_TIME_COMPETITION notification to competition creator
                    try {
                        var creator = userRepository.findByUsernameIgnoreCase(competition.getCreatedBy());
                        if (creator.isPresent()) {
                            String title = "Competition Ended";
                            String message = "The competition '" + competition.getName() + 
                                           "' has ended. Would you like to generate certificates for competitors?";
                            
                            notificationService.createNotification(
                                    creator.get(),
                                    title,
                                    message,
                                    NotificationType.END_TIME_COMPETITION.getCode());
                            
                            // Mark notification as sent to prevent duplicates
                            competition.setEndNotificationSent(true);
                            
                            log.info("Sent END_TIME_COMPETITION notification to admin for competition {}", 
                                    competition.getId());
                        }
                    } catch (Exception e) {
                        log.error("Error sending END_TIME_COMPETITION notification for competition {}: {}", 
                                competition.getId(), e.getMessage());
                    }
                    
                    // Save competition with updated status and notification flag
                    competitionRepository.save(competition);
                }
            }

        } catch (Exception e) {
            log.error("Error in scheduled competition end-time check: {}", e.getMessage(), e);
        }
    }
}
