package com.microslop.service.impl;

import com.microslop.dto.CategoryDTO;
import com.microslop.dto.CompetitionDTO;
import com.microslop.entity.Category;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitionServiceImplTest {

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

    @BeforeEach
    void setUp() {
        creatorUser = new User("Creator User", "creator@example.com", "creator", "password123", LocalDateTime.now().minusYears(25));
        creatorUser.setId(1L);

        judgeUser1 = new User("Judge One", "judge1@example.com", "judge1", "password123", LocalDateTime.now().minusYears(30));
        judgeUser1.setId(2L);

        judgeUser2 = new User("Judge Two", "judge2@example.com", "judge2", "password123", LocalDateTime.now().minusYears(28));
        judgeUser2.setId(3L);

        competitionDTO = new CompetitionDTO();
        competitionDTO.setName("Test Competition");
        competitionDTO.setDescription("A test competition");
        competitionDTO.setStartDate(LocalDateTime.now());
        competitionDTO.setEndDate(LocalDateTime.now().plusDays(7));
        competitionDTO.setEventType("Test Event");

        // Add categories
        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setName("Category 1");
        categoryDTO1.setWeight(50);

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setName("Category 2");
        categoryDTO2.setWeight(50);

        competitionDTO.setCategories(Arrays.asList(categoryDTO1, categoryDTO2));

        // Add judges
        competitionDTO.addJudgeUsername("judge1");
        competitionDTO.addJudgeUsername("judge2");
    }

    @Test
    void should_create_competition_with_categories_and_judges() {
        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(userRepository.findByUsernameIgnoreCase("judge1")).thenReturn(Optional.of(judgeUser1));
        when(userRepository.findByUsernameIgnoreCase("judge2")).thenReturn(Optional.of(judgeUser2));

        Competition savedCompetition = new Competition();
        savedCompetition.setId(1L);
        savedCompetition.setName("Test Competition");
        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    if (c.getId() == null) {
                        c.setId(1L);
                    }
                    return c;
                });

        Competition result = competitionService.createCompetition("creator", competitionDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Competition");
        assertThat(result.getDescription()).isEqualTo("A test competition");
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void should_throw_exception_when_creator_not_found() {
        when(userRepository.findByUsernameIgnoreCase("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competitionService.createCompetition("nonexistent", competitionDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found: nonexistent");
    }

    @Test
    void should_throw_exception_when_judge_not_found() {
        when(userRepository.findByUsernameIgnoreCase("creator")).thenReturn(Optional.of(creatorUser));
        when(userRepository.findByUsernameIgnoreCase("judge1")).thenReturn(Optional.of(judgeUser1));
        when(userRepository.findByUsernameIgnoreCase("judge2")).thenReturn(Optional.empty());

        when(competitionRepository.save(any(Competition.class)))
                .thenAnswer(invocation -> {
                    Competition c = invocation.getArgument(0);
                    if (c.getId() == null) {
                        c.setId(1L);
                    }
                    return c;
                });

        assertThatThrownBy(() -> competitionService.createCompetition("creator", competitionDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Judge user not found: judge2");
    }

    @Test
    void should_save_competition() {
        Competition competition = new Competition();
        competition.setName("Save Test");
        when(competitionRepository.save(competition)).thenReturn(competition);

        Competition result = competitionService.save(competition);

        assertThat(result.getName()).isEqualTo("Save Test");
        verify(competitionRepository).save(competition);
    }

    @Test
    void should_delete_competition() {
        competitionService.delete(1L);

        verify(competitionRepository).deleteById(1L);
    }

    @Test
    void should_activate_competition() {
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setActive(false);
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        Competition result = competitionService.activate(1L);

        assertThat(result.isActive()).isTrue();
        verify(competitionRepository).save(competition);
    }

    @Test
    void should_deactivate_competition() {
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setActive(true);
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        Competition result = competitionService.deactivate(1L);

        assertThat(result.isActive()).isFalse();
        verify(competitionRepository).save(competition);
    }

    @Test
    void should_get_competition_by_id() {
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setName("Test");
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        Optional<Competition> result = competitionService.getById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test");
    }

    @Test
    void should_throw_when_competition_not_found() {
        when(competitionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competitionService.getByIdOrFail(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Competition not found: 999");
    }

    @Test
    void should_get_active_competitions() {
        Competition active1 = new Competition();
        active1.setActive(true);
        active1.setName("Active 1");

        Competition active2 = new Competition();
        active2.setActive(true);
        active2.setName("Active 2");

        List<Competition> activeList = Arrays.asList(active1, active2);
        when(competitionRepository.findByActiveTrue()).thenReturn(activeList);

        List<Competition> result = competitionService.getActiveCompetitions();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Competition::isActive);
    }

    @Test
    void should_get_finished_competitions() {
        Competition finished1 = new Competition();
        finished1.setActive(false);
        finished1.setName("Finished 1");

        Competition finished2 = new Competition();
        finished2.setActive(false);
        finished2.setName("Finished 2");

        List<Competition> finishedList = Arrays.asList(finished1, finished2);
        when(competitionRepository.findByActiveFalse()).thenReturn(finishedList);

        List<Competition> result = competitionService.getFinishedCompetitions();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> !c.isActive());
    }

    @Test
    void should_find_all_competitions() {
        Competition comp1 = new Competition();
        comp1.setName("Comp 1");

        Competition comp2 = new Competition();
        comp2.setName("Comp 2");

        when(competitionRepository.findAll()).thenReturn(Arrays.asList(comp1, comp2));

        List<Competition> result = competitionService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void should_search_competitions_by_name() {
        Competition comp = new Competition();
        comp.setName("Java Competition");

        when(competitionRepository.findByNameIgnoreCase("Java"))
                .thenReturn(Optional.of(comp));

        Optional<Competition> result = competitionService.getById(comp.getId());

        assertThat(result).isPresent();
    }
}
