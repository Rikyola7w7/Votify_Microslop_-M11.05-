package com.microslop.command.ai;

import com.microslop.dto.AiFeedbackResult;
import com.microslop.service.AiFeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateAiFeedbackCommandTest {

    @Mock
    private AiFeedbackService aiFeedbackService;

    @InjectMocks
    private GenerateAiFeedbackCommand command;

    private static final Long PROJECT_ID = 42L;

    @BeforeEach
    void setUp() {
        command = new GenerateAiFeedbackCommand(PROJECT_ID, aiFeedbackService);
    }

    @Test
    @DisplayName("should generate feedback via service on execute")
    void should_generate_feedback_via_service_on_execute() throws Exception {
        AiFeedbackResult expectedResult = AiFeedbackResult.builder()
            .summary("Great project")
            .sentimentScore(4.5)
            .positiveCount(5)
            .neutralCount(2)
            .negativeCount(1)
            .build();

        when(aiFeedbackService.generateFeedbackForProject(PROJECT_ID)).thenReturn(expectedResult);

        AiFeedbackResult result = command.execute();

        assertThat(result).isEqualTo(expectedResult);
        verify(aiFeedbackService, times(1)).generateFeedbackForProject(PROJECT_ID);
    }

    @Test
    @DisplayName("should propagate exception from service")
    void should_propagate_exception_from_service() {
        RuntimeException expectedException = new RuntimeException("Service failed");
        when(aiFeedbackService.generateFeedbackForProject(PROJECT_ID)).thenThrow(expectedException);

        assertThatThrownBy(() -> command.execute())
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Service failed");
    }

    @Test
    @DisplayName("should return correct description")
    void should_return_correct_description() {
        assertThat(command.getDescription()).isEqualTo("Generate AI feedback for project 42");
    }

    @Test
    @DisplayName("should not be undoable")
    void should_not_be_undoable() {
        assertThat(command.isUndoable()).isFalse();
    }

    @Test
    @DisplayName("should throw on undo")
    void should_throw_on_undo() {
        assertThatThrownBy(() -> command.undo())
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessageContaining("does not support undo");
    }

    @Test
    @DisplayName("should redo by re-executing")
    void should_redo_by_re_executing() throws Exception {
        AiFeedbackResult expectedResult = AiFeedbackResult.builder()
            .summary("Great project")
            .sentimentScore(4.0)
            .positiveCount(3)
            .neutralCount(1)
            .negativeCount(0)
            .build();

        when(aiFeedbackService.generateFeedbackForProject(PROJECT_ID)).thenReturn(expectedResult);

        AiFeedbackResult redoResult = command.redo();

        assertThat(redoResult).isEqualTo(expectedResult);
        verify(aiFeedbackService, times(1)).generateFeedbackForProject(PROJECT_ID);
    }
}
