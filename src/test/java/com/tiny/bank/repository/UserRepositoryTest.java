package com.tiny.bank.repository;

import com.tiny.bank.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class UserRepositoryTest {

    private UserRepository victim;

    @BeforeEach
    void setUp() {
        victim = new UserRepository();
    }

    @Test
    void shouldTestUserInsertion() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());

        var result = victim.addUser(user);

        Assertions.assertThat(result)
                .isTrue();
    }

    @Test
    void shouldTestDuplicateUserInsertion() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());
        victim.addUser(user);
        var result = victim.addUser(user);

        Assertions.assertThat(result)
                .isFalse();
    }

    @Test
    void shouldTestUserRetrievalIfExists() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());
        victim.addUser(user);

        var result = victim.getUser("ccNumber");

        Assertions.assertThat(result)
                .isPresent();

        Assertions.assertThat(result.get())
                .isEqualTo(user);
    }

    @Test
    void shouldTestUserRetrievalIfNotExists() {
        var result = victim.getUser("ccNumber");

        Assertions.assertThat(result)
                .isEmpty();
    }

    @Test
    void shouldTestUserUpdateIfNotExists() {
        var result = victim.updateUser("ccNumber", User::deactivateUser);

        Assertions.assertThat(result)
                .isEmpty();
    }

    @Test
    void shouldTestUserUpdateIfExists() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());

        victim.addUser(user);

        var result = victim.updateUser("ccNumber", User::deactivateUser);

        Assertions.assertThat(result)
                .isPresent();

        Assertions.assertThat(result.get())
                .isEqualTo(User.deactivateUser(user));
    }

}
