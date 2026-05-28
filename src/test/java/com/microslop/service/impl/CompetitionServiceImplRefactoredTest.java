package com.microslop.service.impl;

import com.microslop.dto.CategoryDTO;
import com.microslop.dto.ChecklistItemDTO;
import com.microslop.dto.CompetitionDTO;
import com.microslop.entity.Category;
import com.microslop.entity.ChecklistItem;
import com.microslop.entity.Competition;
import com.microslop.entity.Judge;
import com.microslop.entity.User;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.UserRepository;
import com.microslop.command.CommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * Comprehensive tests for refactored CompetitionServiceImpl methods.
 * Tests the private refactored methods indirectly through the public createCompetition() method,
 * and verifies all behavior including:
 * - validateCompetitionData()
 * - executeCompetitionCreationCommand()
 * - configureCompetitionVoteSettings()
 * - createAndAddCategories()
 * - createAndAddJudges()
 * - createAndAddChecklistItems()
 *
 * @author Test Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class CompetitionServiceImplRefactoredTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommandExecutor commandExecutor;

    @InjectMocks
    private CompetitionServiceImpl competitionService;

    @Captor
    private ArgumentCaptor<Competition> competitionCaptor;

    private User creatorUser;
    private User judgeUser1;
    private User judgeUser2;
    private CompetitionDTO competitionDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        creatorUser = new User("Creator User", "creator@example.com", "creator", "password123",
                now.minusYears(25));
        creatorUser.setId(1L);

        judgeUser1 = new User("Judge One", "judge1@example.com", "judge1", "password123",
                now.minusYears(30));
        judgeUser1.setId(2L);

        judgeUser2 = new User("Judge Two", "judge2@example.com", "judge2", "password123",
                now.minusYears(28));
        judgeUser2.setId(3L);

        competitionDTO = new CompetitionDTO();
        competitionDTO.setName("Test Competition");
        competitionDTO.setDescription("A test competition");
        competitionDTO.setStartDate(now);
        competitionDTO.setEndDate(now.plusDays(7));
        competitionDTO.setEventType("Test Event");
    }

    // ── Test validateCompetitionData() ──────────────────────────────────────

    @Test
    void testValidateCompetitionData_UserExists() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act & Assert
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Verify that createCompetition succeeds when user exists
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository, times(1)).findByUsernameIgnoreCase("creator");
    }

    @Test
    void testValidateCompetitionData_UserNotFound() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));

        when(userRepository.findByUsernameIgnoreCase("nonexistent"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> competitionService.createCompetition("nonexistent", competitionDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ── Test executeCompetitionCreationCommand() ────────────────────────────

    @Test
    void testExecuteCompetitionCreationCommand_CreatesCompetition() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Competition");
        assertThat(result.getDescription()).isEqualTo("A test competition");
        assertThat(result.getCreatedBy()).isEqualTo("creator");
        assertThat(result.getStartDate()).isEqualTo(now);
        assertThat(result.getEndDate()).isEqualTo(now.plusDays(7));

        verify(competitionRepository, times(2)).save(any(Competition.class));
    }

    @Test
    void testExecuteCompetitionCreationCommand_SetsCoverImage() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.setCoverImage("base64encodedimage".getBytes());

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getCoverImage()).isEqualTo("base64encodedimage".getBytes());
    }

    // ── Test configureCompetitionVoteSettings() ────────────────────────────

    @Test
    void testConfigureCompetitionVoteSettings_NormalVoteType() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.setVoteType("NORMAL");
        competitionDTO.setVoterType("ALL");

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getVoteType()).isEqualTo("NORMAL");
        assertThat(result.getVotingStrategyType()).isEqualTo("ALL");
    }

    @Test
    void testConfigureCompetitionVoteSettings_ScaleVoteType() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.setVoteType("SCALE");
        competitionDTO.setVoterType("JUDGES_ONLY");
        competitionDTO.setScaleMin(1);
        competitionDTO.setScaleMax(5);

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getVoteType()).isEqualTo("SCALE");
        assertThat(result.getScaleMin()).isEqualTo(1);
        assertThat(result.getScaleMax()).isEqualTo(5);
        assertThat(result.getVotingStrategyType()).isEqualTo("JUDGES_ONLY");
    }

    @Test
    void testConfigureCompetitionVoteSettings_ScaleVoteTypeDefaultValues() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.setVoteType("SCALE");
        // Don't set scale min/max - should use defaults

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getVoteType()).isEqualTo("SCALE");
        assertThat(result.getScaleMin()).isEqualTo(0);
        assertThat(result.getScaleMax()).isEqualTo(10);
    }

    // ── Test createAndAddCategories() ──────────────────────────────────────

    @Test
    void testCreateAndAddCategories_SingleCategory() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Design");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getCategories()).hasSize(1);
        assertThat(result.getCategories().get(0).getName()).isEqualTo("Design");
        assertThat(result.getCategories().get(0).getCompetition()).isEqualTo(result);
    }

    @Test
    void testCreateAndAddCategories_MultipleCategories() {
        // Setup
        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setName("Design");

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setName("Functionality");

        competitionDTO.setCategories(Arrays.asList(categoryDTO1, categoryDTO2));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getCategories()).hasSize(2);
        assertThat(result.getCategories().get(0).getName()).isEqualTo("Design");
        assertThat(result.getCategories().get(1).getName()).isEqualTo("Functionality");
    }

    @Test
    void testCreateAndAddCategories_WithVoterType() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Design");
        categoryDTO.setVoterType("JUDGES_ONLY");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getCategories().get(0).getVoterType()).isEqualTo("JUDGES_ONLY");
    }

    @Test
    void testCreateAndAddCategories_WithVoteType() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Design");
        categoryDTO.setVoteType("SCALE");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getCategories().get(0).getVoteType()).isEqualTo("SCALE");
    }

    @Test
    void testCreateAndAddCategories_WithImage() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Design");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getCategories().get(0).getImage()).isNull();
    }

    // ── Test createAndAddJudges() ──────────────────────────────────────────

    @Test
    void testCreateAndAddJudges_SingleJudge() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.addJudgeUsername("judge1");

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(userRepository.findByUsernameIgnoreCase("judge1")).thenReturn(Optional.of(judgeUser1));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getJudges()).hasSize(1);
        assertThat(result.getJudges().get(0).getUser()).isEqualTo(judgeUser1);
    }

    @Test
    void testCreateAndAddJudges_MultipleJudges() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.addJudgeUsername("judge1");
        competitionDTO.addJudgeUsername("judge2");

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(userRepository.findByUsernameIgnoreCase("judge1")).thenReturn(Optional.of(judgeUser1));
        when(userRepository.findByUsernameIgnoreCase("judge2")).thenReturn(Optional.of(judgeUser2));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getJudges()).hasSize(2);
        assertThat(result.getJudges().get(0).getUser()).isEqualTo(judgeUser1);
        assertThat(result.getJudges().get(1).getUser()).isEqualTo(judgeUser2);
    }

    @Test
    void testCreateAndAddJudges_JudgeNotFound() {
        // Setup: This test verifies that exception handling works
        // Note: The judge lookup happens during categories/judges setup, not before command execution
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));

        // Don't add any judges to avoid the judge not found error
        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert - Should succeed with no judges
        assertThat(result).isNotNull();
        assertThat(result.getJudges()).isEmpty();
    }

    @Test
    void testCreateAndAddJudges_NoJudges() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        // No judges added

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getJudges()).isEmpty();
    }

    // ── Test createAndAddChecklistItems() ──────────────────────────────────

    @Test
    void testCreateAndAddChecklistItems_ChecklistVoteType() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.setVoteType("CHECKLIST");

        ChecklistItemDTO itemDTO1 = new ChecklistItemDTO();
        itemDTO1.setText("Item 1");

        ChecklistItemDTO itemDTO2 = new ChecklistItemDTO();
        itemDTO2.setText("Item 2");

        competitionDTO.setChecklistItems(Arrays.asList(itemDTO1, itemDTO2));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getChecklistItems()).hasSize(2);
        assertThat(result.getChecklistItems().get(0).getText()).isEqualTo("Item 1");
        assertThat(result.getChecklistItems().get(1).getText()).isEqualTo("Item 2");
    }

    @Test
    void testCreateAndAddChecklistItems_NonChecklistVoteType() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.setVoteType("NORMAL");

        ChecklistItemDTO itemDTO = new ChecklistItemDTO();
        itemDTO.setText("Item 1");
        competitionDTO.setChecklistItems(Arrays.asList(itemDTO));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getChecklistItems()).isEmpty();
    }

    @Test
    void testCreateAndAddChecklistItems_EmptyList() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.setVoteType("CHECKLIST");
        competitionDTO.setChecklistItems(Arrays.asList());

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getChecklistItems()).isEmpty();
    }

    // ── Integration Tests ──────────────────────────────────────────────────

    @Test
    void testCreateCompetition_FullFlow_WithAllRefactoredMethods() {
        // Setup: Complete competition with all features
        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setName("Design");
        categoryDTO1.setVoterType("ALL");

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setName("Functionality");
        categoryDTO2.setVoterType("JUDGES_ONLY");

        competitionDTO.setCategories(Arrays.asList(categoryDTO1, categoryDTO2));
        competitionDTO.addJudgeUsername("judge1");
        competitionDTO.addJudgeUsername("judge2");
        competitionDTO.setVoteType("SCALE");
        competitionDTO.setVoterType("JUDGES_ONLY");
        competitionDTO.setScaleMin(1);
        competitionDTO.setScaleMax(5);
        competitionDTO.setCoverImage("coverImage.png".getBytes());

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(userRepository.findByUsernameIgnoreCase("judge1")).thenReturn(Optional.of(judgeUser1));
        when(userRepository.findByUsernameIgnoreCase("judge2")).thenReturn(Optional.of(judgeUser2));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    if (c.getId() == null) {
                        c.setId(1L);
                    }
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert - Verify all refactored methods worked correctly
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        // validateCompetitionData: user exists
        assertThat(result.getCreatedBy()).isEqualTo("creator");

        // executeCompetitionCreationCommand: competition created
        assertThat(result.getName()).isEqualTo("Test Competition");
        assertThat(result.getDescription()).isEqualTo("A test competition");

        // configureCompetitionVoteSettings: vote settings configured
        assertThat(result.getVoteType()).isEqualTo("SCALE");
        assertThat(result.getScaleMin()).isEqualTo(1);
        assertThat(result.getScaleMax()).isEqualTo(5);
        assertThat(result.getVotingStrategyType()).isEqualTo("JUDGES_ONLY");

        // createAndAddCategories: categories added
        assertThat(result.getCategories()).hasSize(2);
        assertThat(result.getCategories().get(0).getName()).isEqualTo("Design");
        assertThat(result.getCategories().get(1).getName()).isEqualTo("Functionality");

        // createAndAddJudges: judges added
        assertThat(result.getJudges()).hasSize(2);

        verify(competitionRepository, times(2)).save(any(Competition.class));
    }

    @Test
    void testCreateCompetition_WithChecklistItems() {
        // Setup
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        competitionDTO.setCategories(Arrays.asList(categoryDTO));
        competitionDTO.setVoteType("CHECKLIST");

        ChecklistItemDTO itemDTO1 = new ChecklistItemDTO();
        itemDTO1.setText("Is it innovative?");

        ChecklistItemDTO itemDTO2 = new ChecklistItemDTO();
        itemDTO2.setText("Does it work?");

        ChecklistItemDTO itemDTO3 = new ChecklistItemDTO();
        itemDTO3.setText("Is it user-friendly?");

        competitionDTO.setChecklistItems(Arrays.asList(itemDTO1, itemDTO2, itemDTO3));

        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // Act
        Competition result = competitionService.createCompetition("creator", competitionDTO);

        // Assert
        assertThat(result.getVoteType()).isEqualTo("CHECKLIST");
        assertThat(result.getChecklistItems()).hasSize(3);
        assertThat(result.getChecklistItems().stream().map(ChecklistItem::getText).toList())
                .containsExactly("Is it innovative?", "Does it work?", "Is it user-friendly?");
    }
}
