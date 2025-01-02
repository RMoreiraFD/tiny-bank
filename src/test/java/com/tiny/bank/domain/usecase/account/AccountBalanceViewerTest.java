package com.tiny.bank.domain.usecase.account;

import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.exception.UserNotAvailableException;
import com.tiny.bank.domain.user.State;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class AccountBalanceViewerTest {

    @Mock
    private UserRepository repositoryMock;
    private AccountBalanceViewer victim;

    @BeforeEach
    void setUp() {
        openMocks(this);

        victim = new AccountBalanceViewer(repositoryMock);
    }

    @Test
    void shouldTestTheViewAccountIfUserPresent() {
        var account = Account.createAccount();
        var user = new User(UUID.randomUUID(), "name", "ccNumber", LocalDate.now(), Set.of(account), State.ACTIVE);

        when(repositoryMock.getUser("ccNumber")).thenReturn(Optional.of(user));

        var result = victim.view(user.ccNumber(), account.getAccountId());

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .isEqualTo(account);
    }

    @Test
    void shouldTestTheViewAccountIfUserNotPresent() {
        when(repositoryMock.getUser(anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> victim.view("ccNumber", UUID.randomUUID()))
                .isInstanceOf(UserNotAvailableException.class);
    }

    @Test
    void shouldTestTheViewAllAccountIfUserPresent() {
        var account = Account.createAccount();
        var secondAccount = Account.createAccount();
        var user = new User(UUID.randomUUID(), "name", "ccNumber", LocalDate.now(), Set.of(account, secondAccount), State.ACTIVE);

        when(repositoryMock.getUser("ccNumber")).thenReturn(Optional.of(user));

        var result = victim.viewAllAccounts(user.ccNumber());

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .containsExactlyInAnyOrder(account, secondAccount);
    }

    @Test
    void shouldTestTheViewAllAccountIfUserNotPresent() {
        when(repositoryMock.getUser(anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> victim.viewAllAccounts("ccNumber"))
                .isInstanceOf(UserNotAvailableException.class);
    }

}
