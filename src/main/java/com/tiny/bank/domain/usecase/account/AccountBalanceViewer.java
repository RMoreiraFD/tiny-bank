package com.tiny.bank.domain.usecase.account;

import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.exception.UserNotAvailableException;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AccountBalanceViewer {

    private final UserRepository repository;

    public AccountBalanceViewer(final UserRepository repository) {
        this.repository = repository;
    }

    public Account view(final String ccNumber, final UUID accountId) {
        Objects.requireNonNull(ccNumber, "ccNumber shouldn't be null");
        Objects.requireNonNull(accountId, "accountId shouldn't be null");

        return repository.getUser(ccNumber)
                .map(user -> user.findAccount(accountId))
                .orElseThrow(() -> new UserNotAvailableException(ccNumber));
    }

    public Set<Account> viewAllAccounts(final String ccNumber) {
        Objects.requireNonNull(ccNumber, "ccNumber shouldn't be null");

        return repository.getUser(ccNumber)
                .map(User::accounts)
                .orElseThrow(() -> new UserNotAvailableException(ccNumber));
    }

}
