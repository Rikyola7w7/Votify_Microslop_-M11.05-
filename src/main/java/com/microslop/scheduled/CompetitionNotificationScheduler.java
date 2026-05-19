package com.microslop.scheduled;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.repository.CompetitionRepository;
import com.microslop.service.CompetitionNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled task to check for competitions closing soon and send notifications to voters
 * Runs every 5 minutes to find competitions that will close within 1 hour
 */
@Component
public class CompetitionNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(CompetitionNotificationScheduler.class);

    private final CompetitionRepository competitionRepository;
    private final CompetitionNotificationService competitionNotificationService;

    public CompetitionNotificationScheduler(CompetitionRepository competitionRepository,
                                          CompetitionNotificationService competitionNotificationService) {
        this.competitionRepository = competitionRepository;
        this.competitionNotificationService = competitionNotificationService;
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
}
