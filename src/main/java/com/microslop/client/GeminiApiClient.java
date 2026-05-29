package com.microslop.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microslop.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Client for the Gemini API via OpenRouter.
 * Uses RestTemplate to make HTTP calls.
 */
@Component
public class GeminiApiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiApiClient.class);
    private static final String OPENROUTER_ENDPOINT =
        "https://openrouter.ai/api/v1/chat/completions";

    private final String apiKey;
    private final String model;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiApiClient(String apiKey) {
        this(apiKey, "", "google/gemini-2.0-flash-001");
    }

    @Autowired
    public GeminiApiClient(
            @Value("${OPENROUTER_API_KEY:}") String openRouterApiKey,
            @Value("${GEMINI_API_KEY:}") String geminiApiKey,
            @Value("${OPENROUTER_MODEL:google/gemini-2.0-flash-001}") String model) {
        if (openRouterApiKey != null && !openRouterApiKey.isBlank() &&
            !"TU_API_KEY_DE_OPENROUTER".equals(openRouterApiKey) && !"TU_API_KEY_DE_GEMINI".equals(openRouterApiKey)) {
            this.apiKey = openRouterApiKey;
        } else if (geminiApiKey != null && !geminiApiKey.isBlank() &&
                   !"TU_API_KEY_DE_GEMINI".equals(geminiApiKey) && !"TU_API_KEY_DE_OPENROUTER".equals(geminiApiKey)) {
            this.apiKey = geminiApiKey;
        } else {
            this.apiKey = "";
        }
        this.model = model != null && !model.isBlank() ? model : "google/gemini-2.0-flash-001";
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates AI feedback by sending all project comments to OpenRouter.
     *
     * @param commentsText concatenated comments text
     * @return raw JSON response string from OpenRouter/Gemini
     */
    public String generateFeedback(String commentsText) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ExternalServiceException("OpenRouter", "API key is not configured. Please set OPENROUTER_API_KEY in .env");
        }

        String prompt = buildPrompt(commentsText);

        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.3,
            "max_tokens", 2048,
            "response_format", Map.of("type", "json_object")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "Votify");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Sending request to OpenRouter API ({}) for feedback generation", model);
            ResponseEntity<String> response = restTemplate.exchange(OPENROUTER_ENDPOINT, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return extractTextFromResponse(response.getBody());
            }
            throw new ExternalServiceException("OpenRouter", "API returned non-2xx status: " + response.getStatusCode(), response.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            log.error("OpenRouter API client error: {}, response body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 429) {
                log.warn("OpenRouter API rate limit exceeded (429). Retry after a few seconds.");
                throw new ExternalServiceException(
                    "OpenRouter", "AI request limit reached. Please wait a moment and try again.", 429);
            }
            throw new ExternalServiceException("OpenRouter", "API error: " + e.getStatusText(), e);
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling OpenRouter API", e);
            throw new ExternalServiceException("OpenRouter", "Failed to generate AI feedback: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String commentsText) {
        return """
            You are an expert sentiment analyst. Analyze the following user comments about a project/competition delivery.

            Comments:
            %s

            CRITICAL: Respond ONLY with a valid JSON object. NO markdown blocks (no ```json). NO text before or after the JSON.

            REQUIRED exact structure (keys must be in English):
            {
              "summary": "mandatory string",
              "positivePoints": ["string"],
              "negativePoints": ["string"],
              "sentimentScore": number,
              "positiveCount": number,
              "neutralCount": number,
              "negativeCount": number,
              "frequentWords": ["string"]
            }

            Example correct response for 3 comments:
            {
              "summary": "Users appreciate the visual design but report slow initial loading times.",
              "positivePoints": ["Attractive design", "Intuitive navigation"],
              "negativePoints": ["High loading time", "Missing dark mode"],
              "sentimentScore": 3.2,
              "positiveCount": 2,
              "neutralCount": 0,
              "negativeCount": 1,
              "frequentWords": ["design", "loading", "slow", "interface", "responsive"]
            }

            STRICT RULES:
            1. All text content (summary, positivePoints, negativePoints, frequentWords) MUST be in ENGLISH.
            2. "summary" is MANDATORY. Must be a concise summary (max 300 chars). If you cannot generate one, use: "Unable to generate a summary from the available comments." NEVER omit it. NEVER leave it empty.
            3. "positivePoints" and "negativePoints": up to 10 short phrases each. ALWAYS provide at least 1 positive and 1 negative point if any comments exist.
            4. "sentimentScore": decimal number from 0.0 to 5.0.
            5. "positiveCount" + "neutralCount" + "negativeCount" MUST equal the total number of comments analyzed.
            6. "frequentWords" is MANDATORY. List up to 10 frequent and significant words (exclude articles, prepositions, and common connectors: the, and, of, a, to, in, is, etc.). If no relevant words, return []. NEVER omit this field. NEVER leave it null.
            7. Respond ONLY with the JSON object. No explanations, no markdown, no additional text.
            8. Verify your JSON is valid before responding. Ensure summary and frequentWords are ALWAYS present.
            9. If you receive comments, you MUST analyze them and provide meaningful positivePoints and negativePoints. Do NOT return empty arrays unless there are truly no positive or negative aspects.
            """.formatted(commentsText);
    }

    private String extractTextFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode message = choices.get(0).path("message");
                JsonNode content = message.path("content");
                if (content.isTextual()) {
                    return content.asText();
                }
            }
            throw new RuntimeException("Unexpected OpenRouter response structure");
        } catch (Exception e) {
            log.error("Failed to parse OpenRouter response", e);
            throw new RuntimeException("Failed to parse OpenRouter response: " + e.getMessage(), e);
        }
    }
}
