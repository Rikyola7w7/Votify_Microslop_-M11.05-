package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VoteEntityTest {

    private Vote vote;
    private User voter;
    private Project project;
    private Category category;

    @BeforeEach
    void setUp() {
        voter = new User("Voter", "voter@example.com", "voter", "password", LocalDateTime.now().minusYears(25));
        voter.setId(1L);

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
        category.setCompetition(competition);

        vote = new Vote(voter, project, category);
    }

    @Test
    void should_create_vote_with_user_project_category() {
        assertThat(vote.getUser()).isEqualTo(voter);
        assertThat(vote.getProject()).isEqualTo(project);
        assertThat(vote.getCategory()).isEqualTo(category);
    }

    @Test
    void should_create_vote_with_points() {
        Vote voteWithPoints = new Vote(voter, project, category, 10);

        assertThat(voteWithPoints.getPoints()).isEqualTo(10);
    }

    @Test
    void should_set_and_get_id() {
        vote.setId(1L);

        assertThat(vote.getId()).isEqualTo(1L);
    }

    @Test
    void should_set_and_get_vote_date() {
        LocalDateTime voteDate = LocalDateTime.now();
        vote.setVoteDate(voteDate);

        assertThat(vote.getVoteDate()).isEqualTo(voteDate);
    }

     @Test
     void should_have_default_points() {
         assertThat(vote.getPoints()).isEqualTo(1);
     }

    @Test
    void should_update_points() {
        vote.setPoints(25);

        assertThat(vote.getPoints()).isEqualTo(25);
    }

    @Test
    void should_have_vote_date() {
        assertThat(vote.getVoteDate()).isNotNull();
    }

    @Test
    void should_set_and_get_comment() {
        vote.setComment("Great project!");

        assertThat(vote.getComment()).isEqualTo("Great project!");
    }

    @Test
    void should_have_null_comment_initially() {
        Vote voteWithoutComment = new Vote(voter, project, category);

        assertThat(voteWithoutComment.getComment()).isNull();
    }
}
