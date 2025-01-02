package com.tiny.bank.domain.usecase.user;

import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.usecase.OperationStatus;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserDepositProcessorTest {

    @Mock
    private UserRepository repositoryMock;
    @Mock
    private Account accountMock;
    @Mock
    private User userMock;

    private UserDepositProcessor victim;

    @BeforeEach
    void setUp() {
        openMocks(this);

        when(userMock.isUserDeactivated()).thenReturn(false);
        when(userMock.ccNumber()).thenReturn(UUID.randomUUID().toString());

        victim = new UserDepositProcessor(repositoryMock);
    }

    @Test
    void shouldTestTheDeposit() {
        when(accountMock.getAccountId()).thenReturn(UUID.randomUUID());
        when(repositoryMock.getUser(userMock.ccNumber())).thenReturn(Optional.of(userMock));

        when(userMock.deposit(accountMock.getAccountId(), BigDecimal.TEN)).thenReturn(BigDecimal.TEN);

        var result = victim.process(userMock.ccNumber(), accountMock.getAccountId(), BigDecimal.TEN);

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .isEqualTo(OperationStatus.success());

        verify(userMock).deposit(accountMock.getAccountId(), BigDecimal.TEN);
    }


    @Test
    void shouldTestTheUserDeactivationWhenUserDoesntExist() {
        when(accountMock.getAccountId()).thenReturn(UUID.randomUUID());
        when(repositoryMock.getUser(userMock.ccNumber())).thenReturn(Optional.empty());

        var result = victim.process(userMock.ccNumber(), accountMock.getAccountId(), BigDecimal.TEN);

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result.isFailure())
                .isTrue();

        verify(userMock, never()).deposit(any(), any());
    }

}
