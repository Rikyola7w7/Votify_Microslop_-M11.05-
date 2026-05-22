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

        // Not enough comments for meaningful analysis
        if (comments.size() < 3) {
            log.warn("Project {} has only {} comment(s). Minimum 3 required for AI analysis.", projectId, comments.size());
            return AiFeedbackResult.builder()
                .summary("No se puede generar un análisis de IA: se necesitan al menos 3 comentarios para obtener un feedback significativo. Actualmente hay " + comments.size() + " comentario(s).")
                .positivePoints(List.of())
                .negativePoints(List.of())
                .sentimentScore(0.0)
                .positiveCount(0)
                .neutralCount(0)
                .negativeCount(0)
                .frequentWords(List.of())
                .build();
        }

        String commentsText = comments.stream()
            .map(ProjectComment::getCommentText)
            .reduce((a, b) -> a + "\n---\n" + b)
            .orElse("");

        log.debug("Sending {} comments to Gemini for project {}", comments.size(), projectId);

        String rawJson;
        try {
            rawJson = geminiApiClient.generateFeedback(commentsText);
        } catch (GeminiApiException e) {
            log.warn("Gemini API error for project {}: {}", projectId, e.getMessage());
            throw new IllegalStateException(e.getMessage(), e);
        }

        log.debug("Raw Gemini response for project {}: {}", projectId, rawJson);
        AiFeedbackResult result = parseGeminiResponse(rawJson);

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

            JsonResponse response = objectMapper.readValue(cleaned, JsonResponse.class);

            // Validate and apply defaults for mandatory fields
            String summary = response.summary;
            if (summary == null || summary.isBlank()) {
                log.warn("Gemini response missing 'summary', using fallback. Raw JSON: {}", cleaned);
                summary = "No se pudo generar un resumen a partir de los comentarios disponibles.";
            }

            List<String> frequentWords = response.frequentWords;
            if (frequentWords == null) {
                log.warn("Gemini response missing 'frequentWords', using empty list. Raw JSON: {}", cleaned);
                frequentWords = List.of();
            }

            return AiFeedbackResult.builder()
                .summary(summary)
                .positivePoints(response.positivePoints)
                .negativePoints(response.negativePoints)
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

            return AiFeedbackResult.builder()
                .summary(feedback.getSummary())
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
