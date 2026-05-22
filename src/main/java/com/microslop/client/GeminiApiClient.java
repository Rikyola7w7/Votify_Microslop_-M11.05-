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

            IMPORTANTE: Debes responder ÚNICAMENTE con un objeto JSON válido. NO uses bloques markdown (no ```json). NO añadas texto antes o después del JSON.

            Estructura EXACTA obligatoria (los nombres de las claves deben respetarse en inglés):
            {
              "summary": "string obligatorio",
              "positivePoints": ["string"],
              "negativePoints": ["string"],
              "sentimentScore": number,
              "positiveCount": number,
              "neutralCount": number,
              "negativeCount": number,
              "frequentWords": ["string"]
            }

            EJEMPLO de respuesta correcta para 3 comentarios:
            {
              "summary": "Los usuarios valoran positivamente el diseño visual, aunque señalan que la carga inicial es lenta.",
              "positivePoints": ["Diseño atractivo", "Navegación intuitiva"],
              "negativePoints": ["Tiempo de carga alto", "Falta de modo oscuro"],
              "sentimentScore": 3.2,
              "positiveCount": 2,
              "neutralCount": 0,
              "negativeCount": 1,
              "frequentWords": ["diseño", "carga", "lento", "interfaz", "responsive"]
            }

            REGLAS ESTRICTAS:
            1. Todo el contenido textual (summary, positivePoints, negativePoints, frequentWords) debe estar en ESPAÑOL.
            2. "summary" es OBLIGATORIO. Debe ser un resumen conciso (máximo 300 caracteres). Si no puedes generarlo, usa: "No se pudo generar un resumen a partir de los comentarios disponibles." NUNCA lo omitas. NUNCA lo dejes vacío.
            3. "positivePoints" y "negativePoints": máximo 10 frases cortas cada una.
            4. "sentimentScore": número decimal de 0.0 a 5.0.
            5. "positiveCount" + "neutralCount" + "negativeCount" DEBE ser igual al número total de comentarios analizados.
            6. "frequentWords" es OBLIGATORIO. Lista de hasta 10 palabras frecuentes y significativas (excluye artículos, preposiciones y conectores: el, la, y, de, que, en, un, es, etc.). Si no hay palabras relevantes, devuelve []. NUNCA omitas este campo. NUNCA lo dejes null.
            7. Responde ÚNICAMENTE con el objeto JSON. Sin explicaciones, sin markdown, sin texto adicional.
            8. Verifica que tu JSON sea válido antes de responder. Asegúrate de que summary y frequentWords SIEMPRE estén presentes.
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
