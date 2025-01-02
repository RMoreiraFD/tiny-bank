package com.tiny.bank.domain.usecase.account;

import com.tiny.bank.domain.exception.UserNotAvailableException;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class AccountCreatorTest {

    @Mock
    private UserRepository repositoryMock;
    private AccountCreator victim;

    @BeforeEach
    void setUp() {
        openMocks(this);

        victim = new AccountCreator(repositoryMock);
    }

    @Test
    void shouldTestUserAccountCreationGivenThatExists() {
        var user = User.createAccount(User.createUser("name", "ccNumber", LocalDate.now()));

        when(repositoryMock.updateUser(eq("ccNumber"), any())).thenReturn(Optional.of(user));

        var result = victim.create("ccNumber");

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .isEqualTo(user);
    }

    @Test
    void shouldTestUserAccountCreationGivenThatNotExists() {
        var user = User.createAccount(User.createUser("name", "ccNumber", LocalDate.now()));

        when(repositoryMock.updateUser("ccNumber", User::createAccount)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy( () -> victim.create("ccNumber"))
                        .isInstanceOf(UserNotAvailableException.class);
    }

}