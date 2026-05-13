package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectCommentEntityTest {

    private ProjectComment comment;
    private User user;
    private Project project;
    private Category category;

    @BeforeEach
    void setUp() {
        user = new User("Commenter", "commenter@example.com", "commenter", "password", LocalDateTime.now().minusYears(25));
        user.setId(1L);

        Competition competition = new Competition();
        competition.setId(1L);

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setCompetition(competition);

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setCompetition(competition);

        comment = new ProjectComment();
        comment.setUser(user);
        comment.setProject(project);
        comment.setCategory(category);
        comment.setCommentText("Great project!");
    }

    @Test
    void should_create_comment_with_all_fields() {
        assertThat(comment.getUser()).isEqualTo(user);
        assertThat(comment.getProject()).isEqualTo(project);
        assertThat(comment.getCategory()).isEqualTo(category);
        assertThat(comment.getCommentText()).isEqualTo("Great project!");
    }

    @Test
    void should_set_and_get_id() {
        comment.setId(1L);

        assertThat(comment.getId()).isEqualTo(1L);
    }

    @Test
    void should_set_and_get_creation_date() {
        LocalDateTime creationDate = LocalDateTime.now();
        comment.setCreationDate(creationDate);

        assertThat(comment.getCreationDate()).isEqualTo(creationDate);
    }


    @Test
    void should_update_comment_text() {
        comment.setCommentText("Updated comment");

        assertThat(comment.getCommentText()).isEqualTo("Updated comment");
    }

    @Test
    void should_set_and_get_user() {
        User newUser = new User("New User", "new@example.com", "newuser", "password", LocalDateTime.now().minusYears(20));
        newUser.setId(2L);

        comment.setUser(newUser);

        assertThat(comment.getUser()).isEqualTo(newUser);
    }

    @Test
    void should_set_and_get_project() {
        Project newProject = new Project();
        newProject.setId(2L);
        newProject.setName("New Project");

        comment.setProject(newProject);

        assertThat(comment.getProject()).isEqualTo(newProject);
    }

    @Test
    void should_set_and_get_category() {
        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("New Category");

        comment.setCategory(newCategory);

        assertThat(comment.getCategory()).isEqualTo(newCategory);
    }
}
