package com.tiny.bank.domain.usecase.transaction;

import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.exception.UserNotAvailableException;
import com.tiny.bank.domain.transaction.Transaction;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class TransactionProcessorTest {

    @Mock
    private UserRepository repositoryMock;
    @Mock
    private User senderMock;
    @Mock
    private Account senderAccountMock;
    @Mock
    private User receiverMock;
    @Mock
    private Account receiverAccountMock;
    @Mock
    private Transaction transactionMock;

    private TransactionProcessor victim;

    @BeforeEach
    void setUp() {
        openMocks(this);

        when(senderMock.isUserDeactivated()).thenReturn(false);
        when(receiverMock.isUserDeactivated()).thenReturn(false);

        victim = new TransactionProcessor(repositoryMock);
    }

    @Test
    void shouldTestATransactionBetweenTwoDifferentUsers() {
        when(transactionMock.isTransactionBetweenUserAccounts()).thenReturn(false);

        when(transactionMock.providerId()).thenReturn("senderId");
        when(transactionMock.recipientId()).thenReturn("receiverId");
        when(transactionMock.providerAccountId()).thenReturn(UUID.randomUUID());
        when(transactionMock.recipientAccountId()).thenReturn(UUID.randomUUID());
        when(transactionMock.amount()).thenReturn(BigDecimal.TEN);

        when(repositoryMock.getUser(transactionMock.providerId())).thenReturn(Optional.of(senderMock));
        when(repositoryMock.getUser(transactionMock.recipientId())).thenReturn(Optional.of(receiverMock));

        when(receiverMock.findAccount(transactionMock.recipientAccountId())).thenReturn(receiverAccountMock);
        when(senderMock.findAccount(transactionMock.providerAccountId())).thenReturn(senderAccountMock);

        victim.process(transactionMock);

        verify(senderAccountMock).transferTo(BigDecimal.TEN, receiverAccountMock);
    }

    @Test
    void shouldTestATransactionBetweenSameUserAccounts() {
        when(transactionMock.isTransactionBetweenUserAccounts()).thenReturn(true);

        when(transactionMock.providerId()).thenReturn("senderId");
        when(transactionMock.providerAccountId()).thenReturn(UUID.randomUUID());
        when(transactionMock.recipientAccountId()).thenReturn(UUID.randomUUID());
        when(transactionMock.amount()).thenReturn(BigDecimal.TEN);

        when(repositoryMock.getUser(transactionMock.providerId())).thenReturn(Optional.of(senderMock));

        when(receiverMock.findAccount(transactionMock.recipientAccountId())).thenReturn(receiverAccountMock);
        when(senderMock.findAccount(transactionMock.providerAccountId())).thenReturn(senderAccountMock);

        victim.process(transactionMock);

        verify(senderMock).transferBetweenAccounts(BigDecimal.TEN, transactionMock.providerAccountId(), transactionMock.recipientAccountId());
    }

    @Test
    void shouldTestATransactionBetweenTwoDifferentUsersFailsIfOneIsNotFound() {
        when(transactionMock.isTransactionBetweenUserAccounts()).thenReturn(false);

        when(transactionMock.providerId()).thenReturn("senderId");
        when(transactionMock.recipientId()).thenReturn("receiverId");
        when(transactionMock.providerAccountId()).thenReturn(UUID.randomUUID());
        when(transactionMock.recipientAccountId()).thenReturn(UUID.randomUUID());
        when(transactionMock.amount()).thenReturn(BigDecimal.TEN);

        when(repositoryMock.getUser(transactionMock.providerId())).thenReturn(Optional.of(senderMock));
        when(repositoryMock.getUser(transactionMock.recipientId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> victim.process(transactionMock))
                .isInstanceOf(UserNotAvailableException.class);

        verify(senderAccountMock, never()).transferTo(BigDecimal.TEN, receiverAccountMock);
    }

    @Test
    void shouldTestATransactionFailsBetweenSameUserAccountsIfUserIsNotFound() {
        when(transactionMock.isTransactionBetweenUserAccounts()).thenReturn(true);

        when(transactionMock.providerId()).thenReturn("senderId");
        when(transactionMock.providerAccountId()).thenReturn(UUID.randomUUID());
        when(transactionMock.recipientAccountId()).thenReturn(UUID.randomUUID());
        when(transactionMock.amount()).thenReturn(BigDecimal.TEN);

        when(repositoryMock.getUser(transactionMock.providerId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> victim.process(transactionMock))
                .isInstanceOf(UserNotAvailableException.class);

        verify(senderMock, never()).transferBetweenAccounts(BigDecimal.TEN, transactionMock.providerAccountId(), transactionMock.recipientAccountId());
    }

}
