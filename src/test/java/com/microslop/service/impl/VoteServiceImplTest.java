package com.microslop.service.impl;

import com.microslop.entity.*;
import com.microslop.factory.VoteCreator;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.VoteRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.command.CommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @Mock
    private VoteCreator voteCreator;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CommandExecutor commandExecutor;

    @InjectMocks
    private VoteServiceImpl voteService;

    private User voter;
    private User creator;
    private Project project;
    private Competition competition;
    private Category category;
    private Vote vote;

    @BeforeEach
    void setUp() {
        voter = new User("Voter", "voter@example.com", "voter", "password123", LocalDateTime.now().minusYears(25));
        voter.setId(1L);

        creator = new User("Creator", "creator@example.com", "creator", "password123", LocalDateTime.now().minusYears(25));
        creator.setId(2L);

        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");
        competition.setActive(true);
        competition.setEndDate(LocalDateTime.now().plusDays(7));

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setWeight(100);

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setCompetition(competition);

        vote = new Vote(voter, project, category);
        vote.setId(1L);
    }

     @Test
     void should_submit_vote_successfully() throws Exception {
         lenient().when(userService.searchByUsernameIgnoreCase("voter")).thenReturn(Optional.of(voter));
         lenient().when(projectService.getById(1L)).thenReturn(project);
         lenient().when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
         lenient().when(voteRepository.countByUserIdAndCategoryId(1L, 1L)).thenReturn(0L);
         lenient().when(voteCreator.create(voter, project, category)).thenReturn(vote);
         when(commandExecutor.execute(any())).thenAnswer(invocation -> {
             Vote v = vote;
             v.setId(1L);
             return v;
         });

         voteService.submitVote("voter", 1L, 1L);

         verify(commandExecutor).execute(any());
     }

     @Test
     void should_throw_when_user_not_found() throws Exception {
         doThrow(new IllegalStateException("User not found."))
                 .when(commandExecutor).execute(any());

         assertThatThrownBy(() -> voteService.submitVote("nonexistent", 1L, 1L))
                 .isInstanceOf(IllegalStateException.class);
     }

     @Test
     void should_throw_when_project_not_found() throws Exception {
         lenient().when(userService.searchByUsernameIgnoreCase("voter")).thenReturn(Optional.of(voter));
         lenient().when(projectService.getById(999L)).thenReturn(null);
         doThrow(new IllegalStateException("Project not found"))
                 .when(commandExecutor).execute(any());

         assertThatThrownBy(() -> voteService.submitVote("voter", 999L, 1L))
                 .isInstanceOf(IllegalStateException.class);
     }

     @Test
     void should_throw_when_category_not_found() throws Exception {
         lenient().when(userService.searchByUsernameIgnoreCase("voter")).thenReturn(Optional.of(voter));
         lenient().when(projectService.getById(1L)).thenReturn(project);
         lenient().when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
         doThrow(new IllegalStateException("Category not found."))
                 .when(commandExecutor).execute(any());

         assertThatThrownBy(() -> voteService.submitVote("voter", 1L, 999L))
                 .isInstanceOf(IllegalStateException.class);
     }

     @Test
     void should_throw_when_competition_not_active() throws Exception {
         competition.setActive(false);
         lenient().when(userService.searchByUsernameIgnoreCase("voter")).thenReturn(Optional.of(voter));
         lenient().when(projectService.getById(1L)).thenReturn(project);
         lenient().when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
         doThrow(new IllegalStateException("Competition is not active"))
                 .when(commandExecutor).execute(any());

         assertThatThrownBy(() -> voteService.submitVote("voter", 1L, 1L))
                 .isInstanceOf(IllegalStateException.class);
     }

     @Test
     void should_throw_when_user_already_voted_in_category() throws Exception {
         lenient().when(userService.searchByUsernameIgnoreCase("voter")).thenReturn(Optional.of(voter));
         lenient().when(projectService.getById(1L)).thenReturn(project);
         lenient().when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
         lenient().when(voteRepository.countByUserIdAndCategoryId(1L, 1L)).thenReturn(1L);
         doThrow(new IllegalStateException("You already voted for a project in this category."))
                 .when(commandExecutor).execute(any());

         assertThatThrownBy(() -> voteService.submitVote("voter", 1L, 1L))
                 .isInstanceOf(IllegalStateException.class);
     }

     @Test
     void should_submit_vote_with_points() throws Exception {
         lenient().when(userService.searchByUsernameIgnoreCase("voter")).thenReturn(Optional.of(voter));
         lenient().when(projectService.getById(1L)).thenReturn(project);
         lenient().when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
         lenient().when(voteRepository.countByUserIdAndCategoryId(1L, 1L)).thenReturn(0L);
         when(commandExecutor.execute(any())).thenAnswer(invocation -> {
             return new Vote();
         });

         voteService.submitVote("voter", 1L, 1L, 10);

         verify(commandExecutor).execute(any());
     }

    @Test
    void should_count_votes_by_project() {
        when(voteRepository.countByProjectId(1L)).thenReturn(5L);

        long result = voteService.countVotesByProject(1L);

        assert result == 5L;
    }

    @Test
    void should_count_votes_by_project_and_category() {
        when(voteRepository.countByProjectIdAndCategoryId(1L, 1L)).thenReturn(3L);

        long result = voteService.countVotesByProjectAndCategory(1L, 1L);

        assert result == 3L;
    }

    @Test
    void should_count_votes_by_user_and_project() {
        when(voteRepository.countByUserIdAndProjectId(1L, 1L)).thenReturn(1L);

        long result = voteService.countVotesByUserAndProject(1L, 1L);

        assert result == 1L;
    }

    @Test
    void should_count_votes_per_user_in_competition() {
        when(voteRepository.countByUserInCompetition(1L, 1L)).thenReturn(5L);

        long result = voteService.countVotesPerUserInCompetition(1L, 1L);

        assert result == 5L;
    }

    @Test
    void should_count_votes_by_user_and_category() {
        when(voteRepository.countByUserIdAndCategoryId(1L, 1L)).thenReturn(2L);

        long result = voteService.countVotesByUserAndCategory(1L, 1L);

        assert result == 2L;
    }

    @Test
    void should_count_votes_by_user_project_and_category() {
        when(voteRepository.countByUserIdAndProjectIdAndCategoryId(1L, 1L, 1L)).thenReturn(1L);

        long result = voteService.countVotesByUserAndProjectAndCategory(1L, 1L, 1L);

        assert result == 1L;
    }

    @Test
    void should_count_points_by_user_and_category() {
        when(voteRepository.sumPointsByUserIdAndCategoryId(1L, 1L)).thenReturn(50L);

        long result = voteService.countPointsByUserAndCategory(1L, 1L);

        assert result == 50L;
    }
}
