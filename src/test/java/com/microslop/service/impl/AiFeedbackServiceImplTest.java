package com.microslop.service.impl;

import com.microslop.client.GeminiApiClient;
import com.microslop.dto.AiFeedbackResult;
import com.microslop.entity.AiFeedback;
import com.microslop.entity.Project;
import com.microslop.entity.ProjectComment;
import com.microslop.entity.User;
import com.microslop.event.AiFeedbackGeneratedEvent;
import com.microslop.repository.AiFeedbackRepository;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AiFeedbackServiceImpl}.
 *
 * <p>Test coverage includes:</p>
 * <ul>
 *   <li>Happy path: generating and persisting feedback from Gemini API</li>
 *   <li>Validation failures: project not found, no comments available</li>
 *   <li>Gemini API error propagation</li>
 *   <li>JSON parsing edge cases: invalid JSON, markdown wrappers</li>
 *   <li>Missing field fallbacks: null/blank summary, null frequentWords</li>
 *   <li>Persistence failure resilience (event still published)</li>
 *   <li>Retrieval operations: existing feedback, missing feedback, existence check</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AiFeedbackServiceImplTest {

    @Mock
    private GeminiApiClient geminiApiClient;

    @Mock
    private ProjectCommentRepository commentRepository;

    @Mock
    private AiFeedbackRepository aiFeedbackRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AiFeedbackServiceImpl aiFeedbackService;

    private Project project;
    private User user;

    @BeforeEach
    void setUp() {
        project = new Project("Test Project", "Description", null);
        project.setId(1L);
        user = new User();
        user.setUsername("testuser");
    }

    @Test
    @DisplayName("should generate and persist feedback when at least 3 comments exist")
    void should_generate_and_persist_feedback() {
        List<ProjectComment> comments = Arrays.asList(
            new ProjectComment(project, user, "Great design!", null),
            new ProjectComment(project, user, "Could be faster", null),
            new ProjectComment(project, user, "Love the UI", null)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);

        String geminiJson = """
            {
              "summary": "Mixed feedback overall",
              "positivePoints": ["Great design"],
              "negativePoints": ["Could be faster"],
              "sentimentScore": 3.5,
              "positiveCount": 2,
              "neutralCount": 0,
              "negativeCount": 1,
              "frequentWords": ["design", "fast"]
            }
            """;
        when(geminiApiClient.generateFeedback(anyString())).thenReturn(geminiJson);

        AiFeedbackResult result = aiFeedbackService.generateFeedbackForProject(1L);

        assertThat(result.getSummary()).isEqualTo("Mixed feedback overall");
        assertThat(result.getSentimentScore()).isEqualTo(3.5);
        assertThat(result.getPositiveCount()).isEqualTo(2);
        assertThat(result.getNeutralCount()).isEqualTo(0);
        assertThat(result.getNegativeCount()).isEqualTo(1);
        assertThat(result.getPositivePoints()).containsExactly("Great design");
        assertThat(result.getNegativePoints()).containsExactly("Could be faster");
        assertThat(result.getFrequentWords()).containsExactly("design", "fast");

        verify(aiFeedbackRepository, times(1)).save(any(AiFeedback.class));

        ArgumentCaptor<AiFeedbackGeneratedEvent> eventCaptor = ArgumentCaptor.forClass(AiFeedbackGeneratedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getProjectId()).isEqualTo(1L);
        assertThat(eventCaptor.getValue().getProjectName()).isEqualTo("Test Project");
    }

    @Test
    @DisplayName("should return insufficient comments message when less than 3 comments")
    void should_return_insufficient_comments_message() {
        List<ProjectComment> comments = Arrays.asList(
            new ProjectComment(project, user, "Great design!", null),
            new ProjectComment(project, user, "Could be faster", null)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);

        AiFeedbackResult result = aiFeedbackService.generateFeedbackForProject(1L);

        assertThat(result).isNotNull();
        assertThat(result.getSummary()).contains("at least 3 comments");
        assertThat(result.getSummary()).contains("2 comment(s)");
        assertThat(result.getFrequentWords()).isEmpty();
        assertThat(result.getSentimentScore()).isEqualTo(0.0);
        assertThat(result.getTotalComments()).isEqualTo(0);

        // Gemini should NOT be called and nothing should be persisted
        verify(geminiApiClient, never()).generateFeedback(anyString());
        verify(aiFeedbackRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw when project not found")
    void should_throw_when_project_not_found() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiFeedbackService.generateFeedbackForProject(99L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Project not found");
    }

    @Test
    @DisplayName("should throw when no comments exist")
    void should_throw_when_no_comments() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> aiFeedbackService.generateFeedbackForProject(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No comments found");
    }

    @Test
    @DisplayName("should propagate Gemini API errors")
    void should_propagate_gemini_errors() {
        List<ProjectComment> comments = Arrays.asList(
            new ProjectComment(project, user, "Comment 1", null),
            new ProjectComment(project, user, "Comment 2", null),
            new ProjectComment(project, user, "Comment 3", null)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);
        when(geminiApiClient.generateFeedback(anyString())).thenThrow(new RuntimeException("API down"));

        assertThatThrownBy(() -> aiFeedbackService.generateFeedbackForProject(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("API down");
    }

    @Test
    @DisplayName("should throw on invalid JSON response")
    void should_throw_on_invalid_json() {
        List<ProjectComment> comments = Arrays.asList(
            new ProjectComment(project, user, "Comment 1", null),
            new ProjectComment(project, user, "Comment 2", null),
            new ProjectComment(project, user, "Comment 3", null)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);
        when(geminiApiClient.generateFeedback(anyString())).thenReturn("not-json-at-all");

        assertThatThrownBy(() -> aiFeedbackService.generateFeedbackForProject(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to parse");
    }

    @Test
    @DisplayName("should use fallback summary when Gemini returns null summary")
    void should_use_fallback_when_summary_is_null() {
        List<ProjectComment> comments = Arrays.asList(
            new ProjectComment(project, user, "Comment 1", null),
            new ProjectComment(project, user, "Comment 2", null),
            new ProjectComment(project, user, "Comment 3", null)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);

        String geminiJson = """
            {
              "summary": null,
              "positivePoints": ["Good"],
              "negativePoints": [],
              "sentimentScore": 3.0,
              "positiveCount": 2,
              "neutralCount": 1,
              "negativeCount": 0,
              "frequentWords": ["test"]
            }
            """;
        when(geminiApiClient.generateFeedback(anyString())).thenReturn(geminiJson);

        AiFeedbackResult result = aiFeedbackService.generateFeedbackForProject(1L);

        assertThat(result.getSummary()).isEqualTo("Unable to generate a summary from the available comments.");
        assertThat(result.getFrequentWords()).containsExactly("test");
    }

    @Test
    @DisplayName("should use fallback summary when Gemini returns blank summary")
    void should_use_fallback_when_summary_is_blank() {
        List<ProjectComment> comments = Arrays.asList(
            new ProjectComment(project, user, "Comment 1", null),
            new ProjectComment(project, user, "Comment 2", null),
            new ProjectComment(project, user, "Comment 3", null)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);

        String geminiJson = """
            {
              "summary": "   ",
              "positivePoints": ["Good"],
              "negativePoints": [],
              "sentimentScore": 3.0,
              "positiveCount": 2,
              "neutralCount": 1,
              "negativeCount": 0,
              "frequentWords": ["test"]
            }
            """;
        when(geminiApiClient.generateFeedback(anyString())).thenReturn(geminiJson);

        AiFeedbackResult result = aiFeedbackService.generateFeedbackForProject(1L);

        assertThat(result.getSummary()).isEqualTo("Unable to generate a summary from the available comments.");
    }

    @Test
    @DisplayName("should use empty list fallback when Gemini returns null frequentWords")
    void should_use_empty_list_when_frequentWords_is_null() {
        List<ProjectComment> comments = Arrays.asList(
            new ProjectComment(project, user, "Comment 1", null),
            new ProjectComment(project, user, "Comment 2", null),
            new ProjectComment(project, user, "Comment 3", null)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);

        String geminiJson = """
            {
              "summary": "Some summary",
              "positivePoints": ["Good"],
              "negativePoints": [],
              "sentimentScore": 3.0,
              "positiveCount": 2,
              "neutralCount": 1,
              "negativeCount": 0,
              "frequentWords": null
            }
            """;
        when(geminiApiClient.generateFeedback(anyString())).thenReturn(geminiJson);

        AiFeedbackResult result = aiFeedbackService.generateFeedbackForProject(1L);

        assertThat(result.getSummary()).isEqualTo("Some summary");
        assertThat(result.getFrequentWords()).isNotNull();
        assertThat(result.getFrequentWords()).isEmpty();
    }

    @Test
    @DisplayName("should parse JSON wrapped in markdown code block")
    void should_parse_json_wrapped_in_markdown() {
        List<ProjectComment> comments = Arrays.asList(
            new ProjectComment(project, user, "Comment 1", null),
            new ProjectComment(project, user, "Comment 2", null),
            new ProjectComment(project, user, "Comment 3", null)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);

        String geminiResponse = """
            ```json
            {
              "summary": "Markdown wrapped",
              "positivePoints": ["Nice"],
              "negativePoints": [],
              "sentimentScore": 4.0,
              "positiveCount": 2,
              "neutralCount": 1,
              "negativeCount": 0,
              "frequentWords": ["nice"]
            }
            ```
            """;
        when(geminiApiClient.generateFeedback(anyString())).thenReturn(geminiResponse);

        AiFeedbackResult result = aiFeedbackService.generateFeedbackForProject(1L);

        assertThat(result.getSummary()).isEqualTo("Markdown wrapped");
        assertThat(result.getFrequentWords()).containsExactly("nice");
    }

    @Test
    @DisplayName("should publish event even when persistence fails")
    void should_publish_event_when_persistence_fails() {
        List<ProjectComment> comments = Arrays.asList(
            new ProjectComment(project, user, "Comment 1", null),
            new ProjectComment(project, user, "Comment 2", null),
            new ProjectComment(project, user, "Comment 3", null)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);
        when(geminiApiClient.generateFeedback(anyString())).thenReturn("""
            {
              "summary": "Test",
              "positivePoints": [],
              "negativePoints": [],
              "sentimentScore": 3.0,
              "positiveCount": 2,
              "neutralCount": 1,
              "negativeCount": 0,
              "frequentWords": []
            }
            """);

        // Simulate persistence failure
        when(aiFeedbackRepository.save(any(AiFeedback.class)))
            .thenThrow(new RuntimeException("Database connection lost"));

        AiFeedbackResult result = aiFeedbackService.generateFeedbackForProject(1L);

        assertThat(result).isNotNull();
        assertThat(result.getSummary()).isEqualTo("Test");

        // Event should still be published even if persistence fails
        ArgumentCaptor<AiFeedbackGeneratedEvent> eventCaptor = ArgumentCaptor.forClass(AiFeedbackGeneratedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getProjectId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should return existing feedback when available")
    void should_return_existing_feedback() {
        AiFeedback persisted = new AiFeedback(
            project,
            "Existing summary",
            "[\"point1\"]",
            "[\"point2\"]",
            4.0,
            5,
            2,
            1,
            "[\"word1\"]"
        );
        persisted.setGeneratedAt(LocalDateTime.now());

        when(aiFeedbackRepository.findFirstByProjectIdOrderByGeneratedAtDesc(1L))
            .thenReturn(Optional.of(persisted));

        AiFeedbackResult result = aiFeedbackService.getExistingFeedbackForProject(1L);

        assertThat(result).isNotNull();
        assertThat(result.getSummary()).isEqualTo("Existing summary");
        assertThat(result.getSentimentScore()).isEqualTo(4.0);
        assertThat(result.getPositivePoints()).containsExactly("point1");
        assertThat(result.getNegativePoints()).containsExactly("point2");
        assertThat(result.getFrequentWords()).containsExactly("word1");
    }

    @Test
    @DisplayName("should return null when no existing feedback")
    void should_return_null_when_no_feedback() {
        when(aiFeedbackRepository.findFirstByProjectIdOrderByGeneratedAtDesc(1L))
            .thenReturn(Optional.empty());

        AiFeedbackResult result = aiFeedbackService.getExistingFeedbackForProject(1L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should return true when feedback exists")
    void should_return_true_when_feedback_exists() {
        when(aiFeedbackRepository.existsByProjectId(1L)).thenReturn(true);
        assertThat(aiFeedbackService.hasFeedbackForProject(1L)).isTrue();
    }

    @Test
    @DisplayName("should return false when feedback does not exist")
    void should_return_false_when_feedback_not_exists() {
        when(aiFeedbackRepository.existsByProjectId(1L)).thenReturn(false);
        assertThat(aiFeedbackService.hasFeedbackForProject(1L)).isFalse();
    }
}
