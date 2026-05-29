package com.microslop.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.microslop.exception.ExternalServiceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GeminiApiClient}.
 *
 * <p>Test coverage includes:</p>
 * <ul>
 *   <li>API key validation (missing, empty, placeholder values)</li>
 *   <li>Successful OpenRouter response extraction</li>
 *   <li>HTTP error handling (non-2xx status codes)</li>
 *   <li>Network and RestTemplate failure scenarios</li>
 *   <li>Malformed or unexpected response structures</li>
 *   <li>Markdown-wrapped JSON content extraction</li>
 *   <li>Edge cases: empty choices array, null content field</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class GeminiApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private GeminiApiClient client;

    private static final String API_KEY = "test-api-key-123";

    @BeforeEach
    void setUp() {
        client = new GeminiApiClient(API_KEY);
        ReflectionTestUtils.setField(client, "restTemplate", restTemplate);
    }

    @Test
    @DisplayName("should throw when API key is not configured")
    void should_throw_when_api_key_not_configured() {
        GeminiApiClient emptyClient = new GeminiApiClient("");
        assertThatThrownBy(() -> emptyClient.generateFeedback("some comments"))
            .isInstanceOf(ExternalServiceException.class)
            .hasMessageContaining("API key is not configured");
    }

    @Test
    @DisplayName("should throw when API key is placeholder")
    void should_throw_when_api_key_is_placeholder() {
        GeminiApiClient placeholderClient = new GeminiApiClient("TU_API_KEY_DE_GEMINI");
        assertThatThrownBy(() -> placeholderClient.generateFeedback("some comments"))
            .isInstanceOf(ExternalServiceException.class)
            .hasMessageContaining("API key is not configured");
    }

    @Test
    @DisplayName("should return extracted text from successful response")
    void should_return_text_from_successful_response() {
        String openRouterResponse = """
            {
              "choices": [
                {
                  "message": {
                    "content": "{\\"summary\\": \\"Great project\\", \\"sentimentScore\\": 4.5}"
                  }
                }
              ]
            }
            """;

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(new ResponseEntity<>(openRouterResponse, HttpStatus.OK));

        String result = client.generateFeedback("Comment 1\nComment 2");

        assertThat(result).contains("Great project");
        assertThat(result).contains("4.5");

        verify(restTemplate, times(1)).exchange(
            eq("https://openrouter.ai/api/v1/chat/completions"),
            eq(HttpMethod.POST),
            argThat((HttpEntity<?> entity) -> {
                var auth = entity.getHeaders().getFirst("Authorization");
                return auth != null && auth.equals("Bearer test-api-key-123");
            }),
            eq(String.class)
        );
    }

    @Test
    @DisplayName("should throw when Gemini returns non-2xx status")
    void should_throw_on_non_2xx_status() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.generateFeedback("comments"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("non-2xx status");
    }

    @Test
    @DisplayName("should throw when RestTemplate throws exception")
    void should_throw_when_rest_template_fails() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RuntimeException("Connection timeout"));

        assertThatThrownBy(() -> client.generateFeedback("comments"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Connection timeout");
    }

    @Test
    @DisplayName("should throw when response structure is unexpected")
    void should_throw_on_unexpected_response_structure() {
        String invalidResponse = "{\"invalid\": true}";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(new ResponseEntity<>(invalidResponse, HttpStatus.OK));

        assertThatThrownBy(() -> client.generateFeedback("comments"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Unexpected OpenRouter response");
    }

    @Test
    @DisplayName("should extract JSON from markdown code block wrapper")
    void should_extract_json_from_markdown_wrapper() {
        String openRouterResponse = """
            {
              "choices": [
                {
                  "message": {
                    "content": "```json\\n{\\"summary\\": \\"Wrapped\\", \\"sentimentScore\\": 3.0}\\n```"
                  }
                }
              ]
            }
            """;

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(new ResponseEntity<>(openRouterResponse, HttpStatus.OK));

        String result = client.generateFeedback("comments");

        assertThat(result).contains("Wrapped");
        assertThat(result).contains("3.0");
    }

    @Test
    @DisplayName("should throw when choices array is empty")
    void should_throw_when_choices_is_empty() {
        String emptyChoicesResponse = "{\"choices\": []}";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(new ResponseEntity<>(emptyChoicesResponse, HttpStatus.OK));

        assertThatThrownBy(() -> client.generateFeedback("comments"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Unexpected OpenRouter response");
    }

    @Test
    @DisplayName("should throw when content field is null")
    void should_throw_when_content_is_null() {
        String nullContentResponse = """
            {
              "choices": [
                {
                  "message": {
                    "content": null
                  }
                }
              ]
            }
            """;

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(new ResponseEntity<>(nullContentResponse, HttpStatus.OK));

        assertThatThrownBy(() -> client.generateFeedback("comments"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Unexpected OpenRouter response");
    }
}
