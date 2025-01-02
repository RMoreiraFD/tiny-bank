package com.tiny.bank.domain.usecase.transaction;

import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.exception.UserNotAvailableException;
import com.tiny.bank.domain.transaction.TransactionRecord;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionHistoryViewer {
    private final UserRepository repository;

    public TransactionHistoryViewer(final UserRepository repository) {
        this.repository = repository;
    }

    public Set<TransactionRecord> view(final String ccNumber, final UUID accountId) {
        Objects.requireNonNull(ccNumber, "ccNumber shouldn't be null");
        Objects.requireNonNull(accountId, "accountId shouldn't be null");

        final User user = repository.getUser(ccNumber)
                .orElseThrow(() -> new UserNotAvailableException(ccNumber));

        return user.findAccount(accountId).getTransactions();
    }

    public Set<Set<TransactionRecord>> viewAllUserTransactions(final String ccNumber) {
        Objects.requireNonNull(ccNumber, "ccNumber shouldn't be null");

        final User user = repository.getUser(ccNumber)
                .orElseThrow(() -> new UserNotAvailableException(ccNumber));

        return user.accounts()
                .stream()
                .map(Account::getTransactions)
                .collect(Collectors.toSet());
    }

}
