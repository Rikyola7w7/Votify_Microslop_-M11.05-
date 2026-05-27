package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AiFeedbackEntityTest {

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project("Test Project", "A test project", null);
        project.setId(1L);
    }

    @Test
    @DisplayName("should create AiFeedback with all fields via constructor")
    void should_create_with_all_fields() {
        AiFeedback feedback = new AiFeedback(
            project,
            "Great project overall",
            "[\"Design\", \"Performance\"]",
            "[\"Documentation\"]",
            4.5,
            8,
            3,
            1,
            "[\"design\", \"fast\"]"
        );

        assertThat(feedback.getProject()).isEqualTo(project);
        assertThat(feedback.getSummary()).isEqualTo("Great project overall");
        assertThat(feedback.getPositivePoints()).isEqualTo("[\"Design\", \"Performance\"]");
        assertThat(feedback.getNegativePoints()).isEqualTo("[\"Documentation\"]");
        assertThat(feedback.getSentimentScore()).isEqualTo(4.5);
        assertThat(feedback.getPositiveCount()).isEqualTo(8);
        assertThat(feedback.getNeutralCount()).isEqualTo(3);
        assertThat(feedback.getNegativeCount()).isEqualTo(1);
        assertThat(feedback.getFrequentWords()).isEqualTo("[\"design\", \"fast\"]");
        assertThat(feedback.getGeneratedAt()).isNotNull();
        assertThat(feedback.getGeneratedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("should have null id before persistence")
    void should_have_null_id_before_persistence() {
        AiFeedback feedback = new AiFeedback();
        assertThat(feedback.getId()).isNull();
    }

    @Test
    @DisplayName("should set and get fields correctly")
    void should_set_and_get_fields() {
        AiFeedback feedback = new AiFeedback();
        feedback.setProject(project);
        feedback.setSummary("Updated summary");
        feedback.setSentimentScore(3.2);
        feedback.setPositiveCount(5);
        feedback.setNeutralCount(2);
        feedback.setNegativeCount(3);

        assertThat(feedback.getProject()).isEqualTo(project);
        assertThat(feedback.getSummary()).isEqualTo("Updated summary");
        assertThat(feedback.getSentimentScore()).isEqualTo(3.2);
        assertThat(feedback.getPositiveCount()).isEqualTo(5);
        assertThat(feedback.getNeutralCount()).isEqualTo(2);
        assertThat(feedback.getNegativeCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("should set generated timestamp in constructor")
    void should_set_generated_timestamp() {
        LocalDateTime before = LocalDateTime.now();
        AiFeedback feedback = new AiFeedback(
            project, "Summary", null, null, 3.0, 1, 1, 1, null
        );
        LocalDateTime after = LocalDateTime.now();

        assertThat(feedback.getGeneratedAt()).isAfterOrEqualTo(before);
        assertThat(feedback.getGeneratedAt()).isBeforeOrEqualTo(after);
    }
}
