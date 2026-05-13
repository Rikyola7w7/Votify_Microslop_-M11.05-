package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.entity.Judge;
import com.microslop.entity.User;
import com.microslop.repository.JudgeRepository;
import com.microslop.service.CompetitionService;
import com.microslop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JudgeServiceImplTest {

    @Mock
    private JudgeRepository judgeRepository;

    @Mock
    private UserService userService;

    @Mock
    private CompetitionService competitionService;

    @InjectMocks
    private JudgeServiceImpl judgeService;

    private User judge1;
    private User judge2;
    private Competition competition;
    private Judge judgeEntity1;
    private Judge judgeEntity2;

    @BeforeEach
    void setUp() {
        judge1 = new User("Judge One", "judge1@example.com", "judge1", "password123", LocalDateTime.now().minusYears(30));
        judge1.setId(1L);

        judge2 = new User("Judge Two", "judge2@example.com", "judge2", "password123", LocalDateTime.now().minusYears(28));
        judge2.setId(2L);

        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");
        competition.setActive(true);

        judgeEntity1 = new Judge(judge1, competition);
        judgeEntity1.setId(1L);

        judgeEntity2 = new Judge(judge2, competition);
        judgeEntity2.setId(2L);
    }

    @Test
    void should_get_judges_by_competition() {
        List<Judge> judges = Arrays.asList(judgeEntity1, judgeEntity2);
        when(judgeRepository.findByCompetitionId(1L)).thenReturn(judges);

        List<Judge> result = judgeService.getJudgesByCompetition(1L);

        assertThat(result).hasSize(2);
        assertThat(result).contains(judgeEntity1, judgeEntity2);
    }

    @Test
    void should_return_empty_list_when_no_judges() {
        when(judgeRepository.findByCompetitionId(1L)).thenReturn(Arrays.asList());

        List<Judge> result = judgeService.getJudgesByCompetition(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void should_add_judge_successfully() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(judge1));
        when(competitionService.getById(1L)).thenReturn(Optional.of(competition));
        when(judgeRepository.existsByUserIdAndCompetitionId(1L, 1L)).thenReturn(false);
        when(judgeRepository.save(any(Judge.class))).thenReturn(judgeEntity1);

        Judge result = judgeService.addJudge(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(judge1);
        assertThat(result.getCompetition()).isEqualTo(competition);
        verify(judgeRepository).save(any(Judge.class));
    }

    @Test
    void should_throw_when_user_already_judge() {
        when(judgeRepository.existsByUserIdAndCompetitionId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> judgeService.addJudge(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El usuario ya es juez de esta competencia");
    }

    @Test
    void should_throw_when_user_not_found() {
        when(judgeRepository.existsByUserIdAndCompetitionId(1L, 1L)).thenReturn(false);
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> judgeService.addJudge(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void should_throw_when_competition_not_found() {
        when(judgeRepository.existsByUserIdAndCompetitionId(1L, 1L)).thenReturn(false);
        when(userService.getUserById(1L)).thenReturn(Optional.of(judge1));
        when(competitionService.getById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> judgeService.addJudge(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Competencia no encontrada");
    }

    @Test
    void should_remove_judge() {
        judgeService.removeJudge(1L, 1L);

        verify(judgeRepository).deleteByUserIdAndCompetitionId(1L, 1L);
    }

    @Test
    void should_check_if_user_is_judge() {
        when(judgeRepository.existsByUserIdAndCompetitionId(1L, 1L)).thenReturn(true);

        boolean result = judgeService.isJudge(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void should_check_if_user_is_not_judge() {
        when(judgeRepository.existsByUserIdAndCompetitionId(1L, 1L)).thenReturn(false);

        boolean result = judgeService.isJudge(1L, 1L);

        assertThat(result).isFalse();
    }
}
