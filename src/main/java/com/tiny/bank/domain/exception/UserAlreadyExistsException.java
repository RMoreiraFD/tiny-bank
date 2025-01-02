package com.tiny.bank.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String userId) {
        super(String.format("User with id=%s already exists", userId));
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
