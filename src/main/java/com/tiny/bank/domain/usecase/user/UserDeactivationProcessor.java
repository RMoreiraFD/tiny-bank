package com.tiny.bank.domain.usecase.user;

import com.tiny.bank.domain.exception.UserNotAvailableException;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;

import java.util.Objects;

public class UserDeactivationProcessor {
    private final UserRepository repository;

    public UserDeactivationProcessor(final UserRepository repository) {
        this.repository = repository;
    }

    public User process(final String ccNumber) {
        Objects.requireNonNull(ccNumber, "ccNumber shouldn't be null");

        //Trick to get advantage of ConcurrentMap compute operation atomicity, leaks a bit of logic to the repository...
        return repository.updateUser(ccNumber, User::deactivateUser)
                .orElseThrow(() -> new UserNotAvailableException(ccNumber));
    }

}
