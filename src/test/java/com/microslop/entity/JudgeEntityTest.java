package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JudgeEntityTest {

    private Judge judge;
    private User user;
    private Competition competition;

    @BeforeEach
    void setUp() {
        user = new User("Judge User", "judge@example.com", "judge", "password", LocalDateTime.now().minusYears(30));
        user.setId(1L);

        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");

        judge = new Judge(user, competition);
    }

    @Test
    void should_create_judge_with_user_and_competition() {
        assertThat(judge.getUser()).isEqualTo(user);
        assertThat(judge.getCompetition()).isEqualTo(competition);
    }

    @Test
    void should_set_and_get_id() {
        judge.setId(1L);

        assertThat(judge.getId()).isEqualTo(1L);
    }

    @Test
    void should_update_user() {
        User newUser = new User("New Judge", "newjudge@example.com", "newjudge", "password", LocalDateTime.now().minusYears(28));
        newUser.setId(2L);

        judge.setUser(newUser);

        assertThat(judge.getUser()).isEqualTo(newUser);
        assertThat(judge.getUser().getId()).isEqualTo(2L);
    }

    @Test
    void should_update_competition() {
        Competition newCompetition = new Competition();
        newCompetition.setId(2L);
        newCompetition.setName("New Competition");

        judge.setCompetition(newCompetition);

        assertThat(judge.getCompetition()).isEqualTo(newCompetition);
        assertThat(judge.getCompetition().getId()).isEqualTo(2L);
    }
}
