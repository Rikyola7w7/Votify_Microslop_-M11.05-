package com.microslop.service.impl;

import com.microslop.entity.*;
import com.microslop.enums.NotificationType;
import com.microslop.repository.CertificateRepository;
import com.microslop.repository.CertificateTypeRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.RankingTypeRepository;
import com.microslop.repository.VoteRepository;
import com.microslop.service.CertificatePdfGenerator;
import com.microslop.service.CertificateService;
import com.microslop.service.NotificationService;
import com.microslop.service.RankingService;
import com.microslop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of CertificateService for generating and managing certificates
 * Now uses entity-based types instead of enums
 */
@Service
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private static final Logger log = LoggerFactory.getLogger(CertificateServiceImpl.class);

    private final CertificateRepository certificateRepository;
    private final ProjectRepository projectRepository;
    private final VoteRepository voteRepository;
    private final RankingService rankingService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final CertificatePdfGenerator pdfGenerator;
    private final CertificateTypeRepository certificateTypeRepository;
    private final RankingTypeRepository rankingTypeRepository;

    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                 ProjectRepository projectRepository,
                                 VoteRepository voteRepository,
                                 RankingService rankingService,
                                 NotificationService notificationService,
                                 UserService userService,
                                 CertificatePdfGenerator pdfGenerator,
                                 CertificateTypeRepository certificateTypeRepository,
                                 RankingTypeRepository rankingTypeRepository) {
        this.certificateRepository = certificateRepository;
        this.projectRepository = projectRepository;
        this.voteRepository = voteRepository;
        this.rankingService = rankingService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.pdfGenerator = pdfGenerator;
        this.certificateTypeRepository = certificateTypeRepository;
        this.rankingTypeRepository = rankingTypeRepository;
    }

    @Override
    public void generateCertificatesForCompetition(Long competitionId) {
        log.info("Starting certificate generation for competition: {}", competitionId);

        try {
            // Get all projects in competition
            List<Project> projects = projectRepository.findByCompetitionId(competitionId);
            if (projects.isEmpty()) {
                log.info("No projects found for competition {}", competitionId);
                return;
            }

            // Get competition from first project
            Competition competition = projects.get(0).getCompetition();

            // Recalculate rankings to ensure they're up to date
            rankingService.recalculateRankings(competitionId);

            // Get certificate type entities
            CertificateTypeEntity participantType = certificateTypeRepository.findByCode("PARTICIPANT")
                    .orElseThrow(() -> new IllegalStateException("PARTICIPANT certificate type not found"));
            CertificateTypeEntity judgeWinnerType = certificateTypeRepository.findByCode("JUDGE_WINNER")
                    .orElseThrow(() -> new IllegalStateException("JUDGE_WINNER certificate type not found"));
            CertificateTypeEntity popularWinnerType = certificateTypeRepository.findByCode("POPULAR_WINNER")
                    .orElseThrow(() -> new IllegalStateException("POPULAR_WINNER certificate type not found"));

            // Get ranking type entities
            RankingTypeEntity judgesRanking = rankingTypeRepository.findByCode("JUDGES_RANKING")
                    .orElseThrow(() -> new IllegalStateException("JUDGES_RANKING ranking type not found"));
            RankingTypeEntity popularRanking = rankingTypeRepository.findByCode("POPULAR_RANKING")
                    .orElseThrow(() -> new IllegalStateException("POPULAR_RANKING ranking type not found"));

            int certificatesGenerated = 0;

            // Get all categories in the competition
            Set<Category> categories = new HashSet<>();
            for (Project project : projects) {
                categories.addAll(project.getCategories());
            }

            if (categories.isEmpty()) {
                log.warn("No categories found for competition {}", competitionId);
                return;
            }

            // Track winning categories per project to avoid giving participant certs for those categories
            Set<String> winningProjectCategories = new HashSet<>();

            // Generate winner certificates for each category and ranking type FIRST
            for (Category category : categories) {
                // Find judge ranking winner for this category
                List<Project> judgeRanking = projectRepository.findJudgeRankingByCategory(category.getId());
                if (!judgeRanking.isEmpty()) {
                    Project judgesWinner = judgeRanking.get(0);
                    winningProjectCategories.add(judgesWinner.getId() + "-" + category.getId());
                    certificatesGenerated += createWinnerCertificate(judgesWinner, competition, category,
                            judgeWinnerType, judgesRanking);
                }

                // Find popular ranking winner for this category
                List<Project> popularRankingList = projectRepository.findPopularRankingByCategory(category.getId());
                if (!popularRankingList.isEmpty()) {
                    Project popularWinner = popularRankingList.get(0);
                    winningProjectCategories.add(popularWinner.getId() + "-" + category.getId());
                    certificatesGenerated += createWinnerCertificate(popularWinner, competition, category,
                            popularWinnerType, popularRanking);
                }
            }

            // Generate participant certificates for all project owners/participants in categories they DID NOT win
            for (Project project : projects) {
                // Use participants from the project
                for (User participant : project.getParticipants()) {
                    for (Category category : project.getCategories()) {
                        // Skip if they won in this specific category for this project
                        if (winningProjectCategories.contains(project.getId() + "-" + category.getId())) {
                            continue;
                        }
                        
                        try {
                            // Check if participant certificate already exists
                            Optional<Certificate> existing = certificateRepository
                                    .findByUserIdAndCompetitionIdAndProjectIdAndCategoryIdAndCertificateTypeAndRankingType(
                                            participant.getId(), competitionId, project.getId(), category.getId(),
                                            participantType, judgesRanking);

                            if (!existing.isPresent()) {
                                Certificate cert = new Certificate(
                                        participant, competition, project, category, judgesRanking);
                                cert.setCertificateType(participantType);
                                certificateRepository.save(cert);
                                certificatesGenerated++;
                                sendCertificateNotification(participant, competition, category, participantType, judgesRanking);
                                log.debug("Created participant certificate for user {} in project {} category {}", 
                                        participant.getId(), project.getId(), category.getName());
                            }
                        } catch (Exception e) {
                            log.error("Error creating participant certificate for project {}: {}", 
                                    project.getId(), e.getMessage());
                        }
                    }
                }
            }



            log.info("Successfully generated {} certificates for competition {}", certificatesGenerated, competitionId);

        } catch (Exception e) {
            log.error("Error generating certificates for competition {}: {}", competitionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate certificates: " + e.getMessage(), e);
        }
    }

    /**
     * Create a winner certificate and send notification
     * Notifications are sent only to participants who are part of the winning project
     * (including admin if they are a project participant)
     */
     private int createWinnerCertificate(Project project, Competition competition, Category category,
                                        CertificateTypeEntity certificateType, RankingTypeEntity rankingType) {
         // For winner certificates, create one for each participant/owner of the project
         int created = 0;

         try {
             for (User participant : project.getParticipants()) {
                 // Check if certificate already exists
                 Optional<Certificate> existing = certificateRepository
                         .findByUserIdAndCompetitionIdAndProjectIdAndCategoryIdAndCertificateTypeAndRankingType(
                                 participant.getId(), competition.getId(), project.getId(), category.getId(),
                                 certificateType, rankingType);

                 if (!existing.isPresent()) {
                     Certificate cert = new Certificate(
                             participant, competition, project, category, certificateType, rankingType, 1);
                     certificateRepository.save(cert);

                     // Send CERTIFICATE_SENT notification
                     sendCertificateNotification(participant, competition, category, certificateType, rankingType);

                     log.debug("Created winner certificate for user {} in project {} ({})",
                             participant.getId(), project.getId(), certificateType.getDisplayName());
                     created++;
                 }
             }
         } catch (Exception e) {
             log.error("Error creating winner certificate for project {}: {}", project.getId(), e.getMessage());
         }

         return created;
     }

    /**
     * Send certificate notification to user
     */
    private void sendCertificateNotification(User user, Competition competition, Category category,
                                            CertificateTypeEntity certificateType, RankingTypeEntity rankingType) {
        try {
            String title = "Certificate Received";
            String message = "You have received a " + certificateType.getDisplayName() +
                           " for category '" + category.getName() + "' from the competition '" + competition.getName() + "'";

            notificationService.createNotification(user, title, message, NotificationType.CERTIFICATE_SENT.getCode());
            log.debug("Sent CERTIFICATE_SENT notification to user {} for competition {}", user.getId(), competition.getId());
        } catch (Exception e) {
            log.error("Error sending certificate notification: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certificate> getCertificatesForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return getCertificatesForUser(currentUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certificate> getCertificatesForUser(Long userId) {
        return certificateRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Certificate getCertificateById(Long certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found: " + certificateId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certificate> getCertificatesForCompetition(Long competitionId) {
        return certificateRepository.findByCompetitionId(competitionId);
    }

    @Override
    public byte[] generateCertificatePdf(Long certificateId) {
        Certificate certificate = getCertificateById(certificateId);
        try {
            return pdfGenerator.generateCertificatePdf(certificate);
        } catch (IOException e) {
            log.error("Error generating PDF for certificate {}: {}", certificateId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteCertificate(Long certificateId) {
        certificateRepository.deleteById(certificateId);
        log.debug("Deleted certificate: {}", certificateId);
    }

    @Override
    public void deleteAllCertificatesForCompetition(Long competitionId) {
        List<Certificate> certificates = certificateRepository.findByCompetitionId(competitionId);
        certificateRepository.deleteAll(certificates);
        log.info("Deleted {} certificates for competition {}", certificates.size(), competitionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCertificatesForUser(Long userId) {
        return certificateRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCertificatesForCompetition(Long competitionId) {
        return certificateRepository.countByCompetitionId(competitionId);
    }
}


