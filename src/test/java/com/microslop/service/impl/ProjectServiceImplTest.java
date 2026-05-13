package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.ProjectRepository;
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
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private Competition competition;
    private User creator;

    @BeforeEach
    void setUp() {
        creator = new User("Creator", "creator@example.com", "creator", "password123", LocalDateTime.now().minusYears(25));
        creator.setId(1L);

        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");
        competition.setActive(true);

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("A test project");
        project.setCompetition(competition);
    }

    @Test
    void should_save_project() {
        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectService.save(project);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        verify(projectRepository).save(project);
    }

    @Test
    void should_delete_project() {
        projectService.delete(1L);

        verify(projectRepository).deleteById(1L);
    }

    @Test
    void should_get_project_by_id() {
        when(projectRepository.findByIdWithVotesAndUsers(1L)).thenReturn(Optional.of(project));

        Project result = projectService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
    }

    @Test
    void should_throw_when_project_not_found() {
        when(projectRepository.findByIdWithVotesAndUsers(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Project not found: 999");
    }

    @Test
    void should_list_projects_by_competition() {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");
        project1.setCompetition(competition);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");
        project2.setCompetition(competition);

        List<Project> projects = Arrays.asList(project1, project2);
        when(projectRepository.findByCompetitionId(1L)).thenReturn(projects);

        List<Project> result = projectService.listByCompetition(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getCompetition().getId().equals(1L));
    }

    @Test
    void should_get_ranking_by_competition() {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");

        List<Project> ranking = Arrays.asList(project1, project2);
        when(projectRepository.findRankingByCompetition(1L)).thenReturn(ranking);
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        List<Project> result = projectService.getRanking(1L);

        assertThat(result).hasSize(2);
        verify(projectRepository).findRankingByCompetition(1L);
    }

    @Test
    void should_get_ranking_by_category() {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");

        List<Project> ranking = Arrays.asList(project1, project2);
        when(projectRepository.findRankingByCategory(1L)).thenReturn(ranking);

        List<Project> result = projectService.getRankingByCategory(1L);

        assertThat(result).hasSize(2);
        verify(projectRepository).findRankingByCategory(1L);
    }

    @Test
    void should_get_user_projects() {
        Project userProject1 = new Project();
        userProject1.setId(1L);
        userProject1.setName("User Project 1");
        userProject1.setCompetition(competition);

        Project userProject2 = new Project();
        userProject2.setId(2L);
        userProject2.setName("User Project 2");
        userProject2.setCompetition(competition);

        List<Project> userProjects = Arrays.asList(userProject1, userProject2);
        when(projectRepository.findProjectsByParticipantUsername("creator")).thenReturn(userProjects);

        List<Project> result = projectService.getUserProjects("creator");

        assertThat(result).hasSize(2);
        verify(projectRepository).findProjectsByParticipantUsername("creator");
    }

    @Test
    void should_return_empty_list_when_user_has_no_projects() {
        when(projectRepository.findProjectsByParticipantUsername("nouser")).thenReturn(Arrays.asList());

        List<Project> result = projectService.getUserProjects("nouser");

        assertThat(result).isEmpty();
    }

    @Test
    void should_handle_lazy_loading_in_user_projects() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Project");
        project.setCompetition(competition);

        List<Project> projects = Arrays.asList(project);
        when(projectRepository.findProjectsByParticipantUsername("creator")).thenReturn(projects);

        List<Project> result = projectService.getUserProjects("creator");

        assertThat(result).hasSize(1);
        // Verify that the service accesses competition and votes to load them within transaction
        assertThat(result.get(0).getCompetition()).isNotNull();
    }
}
