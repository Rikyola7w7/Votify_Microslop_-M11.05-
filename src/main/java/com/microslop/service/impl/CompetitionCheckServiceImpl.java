package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.enums.NotificationType;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.UserRepository;
import com.microslop.service.CompetitionCheckService;
import com.microslop.service.CompetitionNotificationService;
import com.microslop.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompetitionCheckServiceImpl implements CompetitionCheckService {

    private static final Logger log = LoggerFactory.getLogger(CompetitionCheckServiceImpl.class);

    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final CompetitionNotificationService competitionNotificationService;
    private final NotificationService notificationService;

    public CompetitionCheckServiceImpl(CompetitionRepository competitionRepository,
                                       UserRepository userRepository,
                                       CompetitionNotificationService competitionNotificationService,
                                       NotificationService notificationService) {
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.competitionNotificationService = competitionNotificationService;
        this.notificationService = notificationService;
    }

    @Override
    public void checkAndProcessCompetitions() {
        try {
            log.debug("Starting page-based check for competition states");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourFromNow = now.plusHours(1);

            List<Competition> openCompetitions = competitionRepository.findByStatusIn(
                List.of(CompetitionStatus.VOTING_OPEN, CompetitionStatus.ACTIVE)
            );

            for (Competition competition : openCompetitions) {
                if (competition.getEndDate() == null) continue;

                // 1. End Time Check
                if (competition.getEndDate().isBefore(now)) {
                    if (competition.isEndNotificationSent()) {
                        continue;
                    }

                    log.info("Competition {} has ended, notifying admin", competition.getId());
                    competition.conclude();

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
                                    NotificationType.END_TIME_COMPETITION.getCode(),
                                    competition);
                            
                            competition.setEndNotificationSent(true);
                        }
                    } catch (Exception e) {
                        log.error("Error sending END_TIME_COMPETITION notification for competition {}: {}", 
                                competition.getId(), e.getMessage());
                    }
                    
                    competitionRepository.save(competition);
                }
                // 2. Closing Soon Check
                else if (competition.getEndDate().isAfter(now) && competition.getEndDate().isBefore(oneHourFromNow)) {
                    if (competition.isClosingSoonNotificationSent()) {
                        continue;
                    }
                    
                    log.info("Competition {} is closing within 1 hour, sending notifications", competition.getId());
                    competitionNotificationService.notifyCompetitionClosingSoon(competition);
                    
                    competition.setClosingSoonNotificationSent(true);
                    competitionRepository.save(competition);
                }
            }
        } catch (Exception e) {
            log.error("Error in competition check service: {}", e.getMessage(), e);
        }
    }
}
