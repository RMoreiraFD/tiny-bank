package com.tiny.bank.domain.usecase.user;

import com.tiny.bank.domain.exception.UserAlreadyExistsException;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;

import java.util.Objects;

public class UserCreator {
    private final UserRepository repository;

    public UserCreator(final UserRepository repository) {
        this.repository = repository;
    }

    public User create(final User user) {
        Objects.requireNonNull(user, "user shouldn't be null");

        if (!repository.addUser(user)) {
            throw new UserAlreadyExistsException(user.ccNumber());
        }

        return user;
    }

}
