package com.tiny.bank.domain.usecase.transaction;

import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.exception.UserNotAvailableException;
import com.tiny.bank.domain.transaction.Transaction;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;

import java.util.Objects;

public class TransactionProcessor {
    private final UserRepository repository;

    public TransactionProcessor(final UserRepository repository) {
        this.repository = repository;
    }

    public void process(final Transaction transaction) {
        Objects.requireNonNull(transaction, "transaction shouldn't be null");

        if (transaction.isTransactionBetweenUserAccounts()) {
            processTransactionFromSameUser(transaction);
        } else {
            processTransactionBetweenUsers(transaction);
        }
    }

    private void processTransactionFromSameUser(final Transaction transaction) {
        final User user = repository.getUser(transaction.providerId())
                .orElseThrow(() -> new UserNotAvailableException(transaction.providerId()));

        user.transferBetweenAccounts(transaction.amount(), transaction.providerAccountId(), transaction.recipientAccountId());
    }

    private void processTransactionBetweenUsers(final Transaction transaction) {
        final Account provider = repository.getUser(transaction.providerId())
                .orElseThrow(() -> new UserNotAvailableException(transaction.providerId()))
                .findAccount(transaction.providerAccountId());


        final Account recipient = repository.getUser(transaction.recipientId())
                .orElseThrow(() -> new UserNotAvailableException(transaction.recipientId()))
                .findAccount(transaction.recipientAccountId());

        provider.transferTo(transaction.amount(), recipient);
    }
}
