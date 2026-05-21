package com.microslop.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Client for the Google Gemini API (Generative Language API).
 * Uses RestTemplate to make HTTP calls to the free tier endpoint.
 */
@Component
public class GeminiApiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiApiClient.class);
    private static final String GEMINI_ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private final String apiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiApiClient(@Value("${GEMINI_API_KEY:}") String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates AI feedback by sending all project comments to Gemini.
     *
     * @param commentsText concatenated comments text
     * @return raw JSON response string from Gemini
     */
    public String generateFeedback(String commentsText) {
        if (apiKey == null || apiKey.isBlank() || "TU_API_KEY_DE_GEMINI".equals(apiKey)) {
            throw new IllegalStateException("Gemini API key is not configured. Please set GEMINI_API_KEY in .env");
        }

        String prompt = buildPrompt(commentsText);

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", prompt)))
            ),
            "generationConfig", Map.of(
                "temperature", 0.3,
                "maxOutputTokens", 2048,
                "responseMimeType", "application/json"
            )
        );

        String url = GEMINI_ENDPOINT + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Sending request to Gemini API for feedback generation");
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return extractTextFromResponse(response.getBody());
            }
            throw new RuntimeException("Gemini API returned non-2xx status: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException("Failed to generate AI feedback: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String commentsText) {
        return """
            You are an expert sentiment analyst. Analyze the following user comments about a project/competition entry.

            Comments:
            %s

            Provide a structured JSON response with EXACTLY these fields:
            {
              "summary": "A very brief 2-3 sentence overall summary of the feedback",
              "positivePoints": ["point 1", "point 2", ...],
              "negativePoints": ["point 1", "point 2", ...],
              "sentimentScore": 3.5,
              "positiveCount": 5,
              "neutralCount": 3,
              "negativeCount": 2,
              "frequentWords": ["word1", "word2", "word3", "word4", "word5"]
            }

            Rules:
            - summary: max 300 characters, concise.
            - positivePoints and negativePoints: arrays of short phrases (max 10 each).
            - sentimentScore: a decimal number from 0.0 to 5.0 (0 = very negative, 5 = very positive).
            - positiveCount, neutralCount, negativeCount: estimated distribution of comment sentiments (must sum to total comments count).
            - frequentWords: up to 10 most repeated meaningful words from the comments (exclude common stop words like "the", "and", etc.).
            - Respond ONLY with the JSON object, no markdown, no explanations.
            """.formatted(commentsText);
    }

    private String extractTextFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    return parts.get(0).path("text").asText();
                }
            }
            throw new RuntimeException("Unexpected Gemini response structure");
        } catch (Exception e) {
            log.error("Failed to parse Gemini response", e);
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage(), e);
        }
    }
}
