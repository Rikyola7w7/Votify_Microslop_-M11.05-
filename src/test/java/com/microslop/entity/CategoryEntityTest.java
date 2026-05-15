package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryEntityTest {

    private Category category;
    private Competition competition;

    @BeforeEach
    void setUp() {
        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");

        category = new Category();
        category.setName("Test Category");
        category.setCompetition(competition);
    }

    @Test
    void should_create_category_with_all_fields() {
        assertThat(category.getName()).isEqualTo("Test Category");
        assertThat(category.getCompetition()).isEqualTo(competition);
    }

    @Test
    void should_set_and_get_id() {
        category.setId(1L);

        assertThat(category.getId()).isEqualTo(1L);
    }

    @Test
    void should_update_category_fields() {
        category.setName("Updated Category");

        assertThat(category.getName()).isEqualTo("Updated Category");
    }

    @Test
    void should_have_votes() {
        User voter = new User("Voter", "voter@example.com", "voter", "password", LocalDateTime.now().minusYears(25));
        voter.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setCompetition(competition);

        Vote vote = new Vote(voter, project, category);
        category.getVotes().add(vote);

        assertThat(category.getVotes()).hasSize(1);
        assertThat(category.getVotes()).contains(vote);
    }

    @Test
    void should_have_empty_votes_list_initially() {
        assertThat(category.getVotes()).isNotNull();
        assertThat(category.getVotes()).isEmpty();
    }

}
