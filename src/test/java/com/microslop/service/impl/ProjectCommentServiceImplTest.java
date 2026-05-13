package com.microslop.service.impl;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.ProjectComment;
import com.microslop.entity.User;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.UserRepository;
import com.microslop.command.CommandExecutor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectCommentServiceImplTest {

    @Mock
    private ProjectCommentRepository commentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CommandExecutor commandExecutor;

    @InjectMocks
    private ProjectCommentServiceImpl projectCommentService;

    private User user;
    private Project project;
    private Category category;
    private ProjectComment comment;

    @BeforeEach
    void setUp() {
        user = new User("Commenter", "commenter@example.com", "commenter", "password123", LocalDateTime.now().minusYears(25));
        user.setId(1L);

        Competition competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setCompetition(competition);

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        comment = new ProjectComment();
        comment.setId(1L);
        comment.setCommentText("Great project!");
        comment.setProject(project);
        comment.setUser(user);
        comment.setCategory(category);
        comment.setCreationDate(LocalDateTime.now().toLocalDate().atStartOfDay());
    }

    @Test
    void should_save_comment_successfully() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByUsernameIgnoreCase("commenter")).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        // Use builder directly, since factory is removed
        comment = ProjectComment.builder()
            .project(project)
            .user(user)
            .commentText("Great project!")
            .category(category)
            .build();

        projectCommentService.saveComment(1L, "commenter", "Great project!", 1L);

        verify(commentRepository).save(comment);
    }

    @Test
    void should_throw_when_project_not_found() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectCommentService.saveComment(999L, "commenter", "Comment", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Project not found: 999");
    }

    @Test
    void should_throw_when_user_not_found() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByUsernameIgnoreCase("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectCommentService.saveComment(1L, "nonexistent", "Comment", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found: nonexistent");
    }

    @Test
    void should_throw_when_category_not_found() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByUsernameIgnoreCase("commenter")).thenReturn(Optional.of(user));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectCommentService.saveComment(1L, "commenter", "Comment", 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category not found: 999");
    }

    @Test
    void should_get_comments_by_project() {
        ProjectComment comment1 = new ProjectComment();
        comment1.setCommentText("Comment 1");
        
        ProjectComment comment2 = new ProjectComment();
        comment2.setCommentText("Comment 2");

        List<ProjectComment> comments = Arrays.asList(comment1, comment2);
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(comments);

        List<ProjectComment> result = projectCommentService.getCommentsByProject(1L);

        assertThat(result).hasSize(2);
        verify(commentRepository).findByProjectIdOrderByCreationDateDesc(1L);
    }

    @Test
    void should_get_comments_by_user() {
        ProjectComment comment1 = new ProjectComment();
        comment1.setCommentText("User Comment 1");
        
        ProjectComment comment2 = new ProjectComment();
        comment2.setCommentText("User Comment 2");

        List<ProjectComment> comments = Arrays.asList(comment1, comment2);
        when(userRepository.findByUsernameIgnoreCase("commenter")).thenReturn(Optional.of(user));
        when(commentRepository.findByUserIdOrderByCreationDateDesc(1L)).thenReturn(comments);

        List<ProjectComment> result = projectCommentService.getCommentsByUser("commenter");

        assertThat(result).hasSize(2);
        verify(commentRepository).findByUserIdOrderByCreationDateDesc(1L);
    }

    @Test
    void should_throw_when_user_not_found_in_get_comments_by_user() {
        when(userRepository.findByUsernameIgnoreCase("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectCommentService.getCommentsByUser("nonexistent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found: nonexistent");
    }

    @Test
    void should_count_comments_by_project() {
        when(commentRepository.countByProjectId(1L)).thenReturn(5L);

        long result = projectCommentService.countCommentsByProject(1L);

        assertThat(result).isEqualTo(5L);
    }

    @Test
    void should_delete_comment() {
        projectCommentService.deleteComment(1L);

        verify(commentRepository).deleteById(1L);
    }

    @Test
    void should_return_empty_list_when_no_comments() {
        when(commentRepository.findByProjectIdOrderByCreationDateDesc(1L)).thenReturn(Arrays.asList());

        List<ProjectComment> result = projectCommentService.getCommentsByProject(1L);

        assertThat(result).isEmpty();
    }
}
