package com.microslop.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AiFeedbackResult} builder pattern and business logic.
 *
 * <p>Test coverage includes:</p>
 * <ul>
 *   <li>Building a fully populated DTO via the builder</li>
 *   <li>Building with omitted optional collections (null defaults)</li>
 *   <li>Building with explicitly empty collections</li>
 *   <li>Business logic: total comments calculation</li>
 * </ul>
 */
class AiFeedbackResultBuilderTest {

    @Test
    @DisplayName("should build AiFeedbackResult with all fields set")
    void should_build_ai_feedback_result_with_all_fields() {
        List<String> positivePoints = Arrays.asList("Good design", "Fast performance");
        List<String> negativePoints = Arrays.asList("Documentation lacking");
        List<String> frequentWords = Arrays.asList("design", "fast", "UI");

        AiFeedbackResult result = AiFeedbackResult.builder()
            .summary("Great project with minor issues")
            .positivePoints(positivePoints)
            .negativePoints(negativePoints)
            .sentimentScore(4.2)
            .positiveCount(8)
            .neutralCount(3)
            .negativeCount(1)
            .frequentWords(frequentWords)
            .build();

        assertThat(result.getSummary()).isEqualTo("Great project with minor issues");
        assertThat(result.getPositivePoints()).containsExactly("Good design", "Fast performance");
        assertThat(result.getNegativePoints()).containsExactly("Documentation lacking");
        assertThat(result.getSentimentScore()).isEqualTo(4.2);
        assertThat(result.getPositiveCount()).isEqualTo(8);
        assertThat(result.getNeutralCount()).isEqualTo(3);
        assertThat(result.getNegativeCount()).isEqualTo(1);
        assertThat(result.getFrequentWords()).containsExactly("design", "fast", "UI");
        assertThat(result.getTotalComments()).isEqualTo(12);
    }

    @Test
    @DisplayName("should build AiFeedbackResult with default null collections")
    void should_build_with_default_null_collections() {
        AiFeedbackResult result = AiFeedbackResult.builder()
            .summary("Summary only")
            .sentimentScore(3.0)
            .positiveCount(1)
            .neutralCount(1)
            .negativeCount(1)
            .build();

        assertThat(result.getSummary()).isEqualTo("Summary only");
        assertThat(result.getPositivePoints()).isNull();
        assertThat(result.getNegativePoints()).isNull();
        assertThat(result.getFrequentWords()).isNull();
        assertThat(result.getTotalComments()).isEqualTo(3);
    }

    @Test
    @DisplayName("should build AiFeedbackResult with empty lists")
    void should_build_with_empty_lists() {
        AiFeedbackResult result = AiFeedbackResult.builder()
            .summary("Empty lists test")
            .positivePoints(List.of())
            .negativePoints(List.of())
            .sentimentScore(0.0)
            .positiveCount(0)
            .neutralCount(0)
            .negativeCount(0)
            .frequentWords(List.of())
            .build();

        assertThat(result.getPositivePoints()).isEmpty();
        assertThat(result.getNegativePoints()).isEmpty();
        assertThat(result.getFrequentWords()).isEmpty();
        assertThat(result.getTotalComments()).isEqualTo(0);
    }

    @Test
    @DisplayName("should calculate total comments correctly")
    void should_calculate_total_comments() {
        AiFeedbackResult result = AiFeedbackResult.builder()
            .summary("Test")
            .positiveCount(5)
            .neutralCount(3)
            .negativeCount(2)
            .build();

        assertThat(result.getTotalComments()).isEqualTo(10);
    }
}
