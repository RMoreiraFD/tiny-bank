package com.tiny.bank.domain.usecase.transaction;

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

class TransactionHistoryViewerTest {

    @Mock
    private UserRepository repositoryMock;
    private TransactionHistoryViewer victim;

    @BeforeEach
    void setUp() {
        openMocks(this);

        victim = new TransactionHistoryViewer(repositoryMock);
    }

    @Test
    void shouldTestTheViewAccountTransactionsIfUserPresent() {
        var account = Account.createAccount();
        var user = new User(UUID.randomUUID(), "name", "ccNumber", LocalDate.now(), Set.of(account), State.ACTIVE);

        when(repositoryMock.getUser("ccNumber")).thenReturn(Optional.of(user));

        var result = victim.view(user.ccNumber(), account.getAccountId());

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .isEqualTo(Set.of());
    }

    @Test
    void shouldTestTheViewAccountTransactionsIfUserNotPresent() {
        when(repositoryMock.getUser(anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> victim.view("ccNumber", UUID.randomUUID()))
                .isInstanceOf(UserNotAvailableException.class);
    }

    @Test
    void shouldTestTheViewAllAccountTransactionsIfUserPresent() {
        var account = Account.createAccount();
        var secondAccount = Account.createAccount();
        var user = new User(UUID.randomUUID(), "name", "ccNumber", LocalDate.now(), Set.of(account, secondAccount), State.ACTIVE);

        when(repositoryMock.getUser("ccNumber")).thenReturn(Optional.of(user));

        var result = victim.viewAllUserTransactions(user.ccNumber());

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .isEqualTo(Set.of(Set.of()));
    }

    @Test
    void shouldTestTheViewAllAccountIfUserNotPresent() {
        when(repositoryMock.getUser(anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> victim.viewAllUserTransactions("ccNumber"))
                .isInstanceOf(UserNotAvailableException.class);
    }

}