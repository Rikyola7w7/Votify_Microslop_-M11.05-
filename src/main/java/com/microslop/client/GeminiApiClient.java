package com.microslop.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            throw new IllegalStateException("OpenRouter/Gemini API key is not configured. Please set OPENROUTER_API_KEY in .env");
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
            throw new GeminiApiException("OpenRouter API returned non-2xx status: " + response.getStatusCode(), response.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            log.error("OpenRouter API client error: {}, response body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 429) {
                log.warn("OpenRouter API rate limit exceeded (429)");
                throw new GeminiApiException(
                    "The AI service is temporarily unavailable due to high demand. Please wait a minute and try again.", 429);
            }
            throw new GeminiApiException("OpenRouter API error: " + e.getStatusText() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Error calling OpenRouter API", e);
            throw new GeminiApiException("Failed to generate AI feedback: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String commentsText) {
        return """
            Eres un analista de sentimientos experto. Analiza los siguientes comentarios de los usuarios sobre la entrega de un proyecto/competencia.

            Comentarios:
            %s

            Debes responder ÚNICAMENTE con un objeto JSON válido que siga EXACTAMENTE esta estructura, respetando los nombres de las claves en inglés tal como están:
            {
              "summary": "Un resumen muy breve (de 2 a 3 frases) en español del análisis de los comentarios",
              "positivePoints": ["punto positivo 1", "punto positivo 2", ...],
              "negativePoints": ["punto negativo 1", "punto negativo 2", ...],
              "sentimentScore": 3.5,
              "positiveCount": 5,
              "neutralCount": 3,
              "negativeCount": 2,
              "frequentWords": ["palabra1", "palabra2", "palabra3", "palabra4", "palabra5"]
            }

            Reglas estrictas:
            1. El idioma de todo el contenido del JSON (los valores de: summary, positivePoints, negativePoints, frequentWords) debe ser estrictamente ESPAÑOL.
            2. "summary": Debe ser un resumen conciso en español del análisis de los comentarios (máximo 300 caracteres).
            3. "positivePoints" y "negativePoints": Listas de frases cortas en español (máximo 10 de cada una) que sinteticen lo bueno y lo malo.
            4. "sentimentScore": Un número decimal del 0.0 al 5.0 (0.0 = extremadamente negativo, 5.0 = extremadamente positivo).
            5. "positiveCount", "neutralCount", "negativeCount": Distribución estimada del sentimiento de los comentarios (la suma de estos tres campos DEBE ser exactamente igual al número total de comentarios analizados).
            6. "frequentWords": Lista de hasta 10 palabras más frecuentes y significativas de los comentarios (excluye preposiciones, artículos y conectores comunes como "el", "y", "de", "que", etc.).
            7. Responde ÚNICAMENTE con el objeto JSON. No incluyas bloques de código markdown (como ```json o ```), ni texto explicativo adicional antes o después del JSON.
            """.formatted(commentsText);
    }

    private String extractTextFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode message = choices.get(0).path("message");
                JsonNode content = message.path("content");
                if (content.isTextual() || !content.isMissingNode()) {
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
