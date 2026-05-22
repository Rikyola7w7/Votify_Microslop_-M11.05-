package com.microslop.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microslop.client.GeminiApiClient;
import com.microslop.client.GeminiApiException;
import com.microslop.dto.AiFeedbackResult;
import com.microslop.entity.AiFeedback;
import com.microslop.entity.Project;
import com.microslop.entity.ProjectComment;
import com.microslop.event.AiFeedbackGeneratedEvent;
import com.microslop.repository.AiFeedbackRepository;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.service.AiFeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AiFeedbackServiceImpl implements AiFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(AiFeedbackServiceImpl.class);

    private final GeminiApiClient geminiApiClient;
    private final ProjectCommentRepository commentRepository;
    private final AiFeedbackRepository aiFeedbackRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public AiFeedbackServiceImpl(GeminiApiClient geminiApiClient,
                                   ProjectCommentRepository commentRepository,
                                   AiFeedbackRepository aiFeedbackRepository,
                                   ProjectRepository projectRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.geminiApiClient = geminiApiClient;
        this.commentRepository = commentRepository;
        this.aiFeedbackRepository = aiFeedbackRepository;
        this.projectRepository = projectRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AiFeedbackResult generateFeedbackForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        List<ProjectComment> comments = commentRepository.findByProjectIdOrderByCreationDateDesc(projectId);
        if (comments.isEmpty()) {
            throw new IllegalStateException("No comments found for this project. Feedback cannot be generated without comments.");
        }

        String commentsText = comments.stream()
            .map(ProjectComment::getCommentText)
            .reduce((a, b) -> a + "\n---\n" + b)
            .orElse("");

        log.info("Generating AI feedback for project {} with {} comments", projectId, comments.size());

        String rawJson;
        try {
            rawJson = geminiApiClient.generateFeedback(commentsText);
        } catch (GeminiApiException e) {
            log.error("Gemini API error for project {}: {}", projectId, e.getMessage());
            throw new IllegalStateException(e.getMessage(), e);
        }

        log.info("Raw Gemini response for project {} (length={}): {}", projectId, rawJson.length(), rawJson.length() > 1000 ? rawJson.substring(0, 1000) + "..." : rawJson);
        AiFeedbackResult result = parseGeminiResponse(rawJson);
        log.info("Parsed AI feedback for project {}: summary='{}', positivePoints={}, negativePoints={}, frequentWords={}, sentiment={}, counts=({}/{}/{})",
            projectId, result.getSummary(), result.getPositivePoints(), result.getNegativePoints(),
            result.getFrequentWords(), result.getSentimentScore(),
            result.getPositiveCount(), result.getNeutralCount(), result.getNegativeCount());

        // Persist
        try {
            String positiveJson = objectMapper.writeValueAsString(result.getPositivePoints());
            String negativeJson = objectMapper.writeValueAsString(result.getNegativePoints());
            String wordsJson = objectMapper.writeValueAsString(result.getFrequentWords());

            AiFeedback feedback = new AiFeedback(
                project,
                result.getSummary(),
                positiveJson,
                negativeJson,
                result.getSentimentScore(),
                result.getPositiveCount(),
                result.getNeutralCount(),
                result.getNegativeCount(),
                wordsJson
            );
            aiFeedbackRepository.save(feedback);
            log.info("AI feedback persisted for project {}", projectId);
        } catch (Exception e) {
            log.error("Failed to persist AI feedback", e);
        }

        // Publish event for observers
        eventPublisher.publishEvent(new AiFeedbackGeneratedEvent(
            projectId, project.getName(), null, result));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public AiFeedbackResult getExistingFeedbackForProject(Long projectId) {
        return aiFeedbackRepository.findFirstByProjectIdOrderByGeneratedAtDesc(projectId)
            .map(this::convertToDto)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasFeedbackForProject(Long projectId) {
        return aiFeedbackRepository.existsByProjectId(projectId);
    }

    private AiFeedbackResult parseGeminiResponse(String rawJson) {
        try {
            // Robust JSON extraction: locate the first '{' and last '}'
            String cleaned = rawJson.trim();
            int firstBrace = cleaned.indexOf('{');
            int lastBrace = cleaned.lastIndexOf('}');
            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                cleaned = cleaned.substring(firstBrace, lastBrace + 1);
            }

            log.info("Parsing Gemini JSON response (length={}): {}", cleaned.length(), cleaned.length() > 500 ? cleaned.substring(0, 500) + "..." : cleaned);

            JsonResponse response = objectMapper.readValue(cleaned, JsonResponse.class);

            // Validate and apply defaults for ALL fields
            String summary = response.summary;
            if (summary == null || summary.isBlank()) {
                log.warn("Gemini response missing 'summary', using fallback.");
                summary = "Unable to generate a summary from the available comments.";
            }

            List<String> frequentWords = response.frequentWords;
            if (frequentWords == null) {
                log.warn("Gemini response missing 'frequentWords', using empty list.");
                frequentWords = List.of();
            }

            List<String> positivePoints = response.positivePoints;
            if (positivePoints == null) {
                log.warn("Gemini response missing 'positivePoints', using empty list.");
                positivePoints = List.of();
            }

            List<String> negativePoints = response.negativePoints;
            if (negativePoints == null) {
                log.warn("Gemini response missing 'negativePoints', using empty list.");
                negativePoints = List.of();
            }

            // Detect completly unusable response: all meaningful fields empty AND zero scores
            boolean isUnusableResponse = summary.isBlank()
                && positivePoints.isEmpty()
                && negativePoints.isEmpty()
                && frequentWords.isEmpty()
                && response.sentimentScore == 0.0;

            if (isUnusableResponse) {
                log.warn("Gemini returned a completely unusable response (all fields empty/zero). Using fallback.");
                return AiFeedbackResult.builder()
                    .summary("AI analysis could not be completed. The service may be temporarily unavailable. Please try again later.")
                    .positivePoints(List.of("Unable to analyze positive aspects."))
                    .negativePoints(List.of("Unable to analyze negative aspects."))
                    .sentimentScore(0.0)
                    .positiveCount(0)
                    .neutralCount(0)
                    .negativeCount(0)
                    .frequentWords(List.of())
                    .build();
            }

            return AiFeedbackResult.builder()
                .summary(summary)
                .positivePoints(positivePoints)
                .negativePoints(negativePoints)
                .sentimentScore(response.sentimentScore)
                .positiveCount(response.positiveCount)
                .neutralCount(response.neutralCount)
                .negativeCount(response.negativeCount)
                .frequentWords(frequentWords)
                .build();
        } catch (Exception e) {
            log.error("Failed to parse Gemini JSON response: {}", rawJson, e);
            throw new RuntimeException("Failed to parse AI feedback response: " + e.getMessage(), e);
        }
    }

    private AiFeedbackResult convertToDto(AiFeedback feedback) {
        try {
            List<String> positive = objectMapper.readValue(feedback.getPositivePoints(), new TypeReference<>() {});
            List<String> negative = objectMapper.readValue(feedback.getNegativePoints(), new TypeReference<>() {});
            List<String> words = objectMapper.readValue(feedback.getFrequentWords(), new TypeReference<>() {});

            // Protect against null/blank persisted values
            String summary = feedback.getSummary();
            if (summary == null || summary.isBlank()) {
                summary = "AI analysis could not be completed. The service may be temporarily unavailable. Please try again later.";
            }
            if (positive == null) {
                positive = List.of();
            }
            if (negative == null) {
                negative = List.of();
            }
            if (words == null) {
                words = List.of();
            }

            return AiFeedbackResult.builder()
                .summary(summary)
                .positivePoints(positive)
                .negativePoints(negative)
                .sentimentScore(feedback.getSentimentScore())
                .positiveCount(feedback.getPositiveCount())
                .neutralCount(feedback.getNeutralCount())
                .negativeCount(feedback.getNegativeCount())
                .frequentWords(words)
                .build();
        } catch (Exception e) {
            log.error("Failed to convert AiFeedback entity to DTO", e);
            throw new RuntimeException("Failed to load persisted feedback", e);
        }
    }

    // Inner class for JSON deserialization
    public static class JsonResponse {
        public String summary;
        public List<String> positivePoints;
        public List<String> negativePoints;
        public double sentimentScore;
        public int positiveCount;
        public int neutralCount;
        public int negativeCount;
        public List<String> frequentWords;
    }
}
