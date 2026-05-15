package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitionEntityTest {

    private Competition competition;
    private User judge;

    @BeforeEach
    void setUp() {
        competition = new Competition();
        competition.setName("Test Competition");
        competition.setDescription("A test competition");
        competition.setActive(true);
        competition.setStartDate(LocalDateTime.now());
        competition.setEndDate(LocalDateTime.now().plusDays(7));

        judge = new User("Judge", "judge@example.com", "judge", "password", LocalDateTime.now().minusYears(30));
        judge.setId(1L);
    }

    @Test
    void should_create_competition_with_all_fields() {
        assertThat(competition.getName()).isEqualTo("Test Competition");
        assertThat(competition.getDescription()).isEqualTo("A test competition");
        assertThat(competition.isActive()).isTrue();
    }

    @Test
    void should_set_and_get_id() {
        competition.setId(1L);

        assertThat(competition.getId()).isEqualTo(1L);
    }

    @Test
    void should_activate_and_deactivate() {
        competition.setActive(false);
        assertThat(competition.isActive()).isFalse();

        competition.setActive(true);
        assertThat(competition.isActive()).isTrue();
    }

    @Test
    void should_add_judge() {
        Judge judgeEntity = new Judge(judge, competition);
        competition.addJudge(judgeEntity);

        assertThat(competition.getJudges()).hasSize(1);
        assertThat(competition.getJudges()).contains(judgeEntity);
    }

    @Test
    void should_remove_judge() {
        Judge judgeEntity = new Judge(judge, competition);
        competition.addJudge(judgeEntity);
        competition.removeJudge(judgeEntity);

        assertThat(competition.getJudges()).isEmpty();
    }

    @Test
    void should_add_project() {
        Project project = new Project();
        project.setName("Test Project");
        project.setCompetition(competition);
        competition.getProjects().add(project);

        assertThat(competition.getProjects()).hasSize(1);
        assertThat(competition.getProjects()).contains(project);
    }

    @Test
    void should_add_category() {
        Category category = new Category();
        category.setName("Test Category");
        category.setCompetition(competition);
        competition.addCategory(category);

        assertThat(competition.getCategories()).hasSize(1);
    }

    @Test
    void should_have_empty_judges_list_initially() {
        assertThat(competition.getJudges()).isNotNull();
        assertThat(competition.getJudges()).isEmpty();
    }

    @Test
    void should_have_empty_projects_list_initially() {
        assertThat(competition.getProjects()).isNotNull();
        assertThat(competition.getProjects()).isEmpty();
    }

    @Test
    void should_set_created_by() {
        competition.setCreatedBy("admin");

        assertThat(competition.getCreatedBy()).isEqualTo("admin");
    }

    @Test
    void should_set_created_by_field() {
        competition.setCreatedBy("admin");

        assertThat(competition.getCreatedBy()).isEqualTo("admin");
    }
}
