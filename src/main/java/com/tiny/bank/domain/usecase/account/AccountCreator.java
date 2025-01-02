package com.tiny.bank.domain.usecase.account;

import com.tiny.bank.domain.exception.UserNotAvailableException;
import com.tiny.bank.domain.usecase.OperationStatus;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;

import java.util.Objects;

public class AccountCreator {

    private final UserRepository repository;

    public AccountCreator(final UserRepository repository) {
        this.repository = repository;
    }

    public User create(final String ccNumber) {
        Objects.requireNonNull(ccNumber, "ccNumber shouldn't be null");

        //Trick to get advantage of ConcurrentMap compute operation atomicity, leaks a bit of logic to the repository...
        return repository.updateUser(ccNumber, User::createAccount)
                .orElseThrow(() -> new UserNotAvailableException(ccNumber));
    }
}
