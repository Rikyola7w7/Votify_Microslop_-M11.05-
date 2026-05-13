package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectEntityTest {

    private Project project;
    private Competition competition;
    private User creator;

    @BeforeEach
    void setUp() {
        creator = new User("Creator", "creator@example.com", "creator", "password", LocalDateTime.now().minusYears(25));
        creator.setId(1L);

        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");

        project = new Project();
        project.setName("Test Project");
        project.setDescription("A test project");
        project.setCompetition(competition);
    }

    @Test
    void should_create_project_with_all_fields() {
        assertThat(project.getName()).isEqualTo("Test Project");
        assertThat(project.getDescription()).isEqualTo("A test project");
        assertThat(project.getCompetition()).isEqualTo(competition);
    }

    @Test
    void should_set_and_get_id() {
        project.setId(1L);

        assertThat(project.getId()).isEqualTo(1L);
    }

    @Test
    void should_add_participant() {
        project.getParticipants().add(creator);

        assertThat(project.getParticipants()).hasSize(1);
        assertThat(project.getParticipants()).contains(creator);
    }

    @Test
    void should_have_votes() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Vote vote = new Vote(creator, project, category);
        project.getVotes().add(vote);

        assertThat(project.getVotes()).hasSize(1);
    }

    @Test
    void should_update_project_fields() {
        project.setName("Updated Project");
        project.setDescription("Updated description");

        assertThat(project.getName()).isEqualTo("Updated Project");
        assertThat(project.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void should_have_empty_participants_list_initially() {
        assertThat(project.getParticipants()).isNotNull();
        assertThat(project.getParticipants()).isEmpty();
    }

    @Test
    void should_have_empty_votes_list_initially() {
        assertThat(project.getVotes()).isNotNull();
        assertThat(project.getVotes()).isEmpty();
    }

}
