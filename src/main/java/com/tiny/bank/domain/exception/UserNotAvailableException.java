package com.tiny.bank.domain.exception;

public class UserNotAvailableException extends RuntimeException {

    public UserNotAvailableException(String userId) {
        super(String.format("User with id=%s doesn't exist", userId));
    }

    public UserNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
