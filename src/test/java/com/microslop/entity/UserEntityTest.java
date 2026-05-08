package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("John Doe", "john@example.com", "johndoe", "hashedPassword", LocalDateTime.now().minusYears(25));
    }

    @Test
    void should_create_user_with_all_fields() {
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getUsername()).isEqualTo("johndoe");
        assertThat(user.getPassword()).isEqualTo("hashedPassword");
        assertThat(user.getBirthDate()).isNotNull();
    }

    @Test
    void should_set_and_get_id() {
        user.setId(1L);

        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    void should_set_and_get_profile_picture() {
        byte[] picture = new byte[]{1, 2, 3};
        user.setProfilePicture(picture);

        assertThat(user.getProfilePicture()).isEqualTo(picture);
    }

    @Test
    void should_set_and_get_creation_date() {
        LocalDateTime now = LocalDateTime.now();
        user.setCreationDate(now);

        assertThat(user.getCreationDate()).isEqualTo(now);
    }

    @Test
    void should_update_user_fields() {
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setUsername("janedoe");

        assertThat(user.getName()).isEqualTo("Jane Doe");
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
        assertThat(user.getUsername()).isEqualTo("janedoe");
    }

    @Test
    void should_have_votes_list() {
        assertThat(user.getVotes()).isNotNull();
        assertThat(user.getVotes()).isEmpty();
    }

    @Test
    void should_have_comments_list() {
        assertThat(user.getComments()).isNotNull();
        assertThat(user.getComments()).isEmpty();
    }
}
