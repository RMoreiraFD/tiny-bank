package com.tiny.bank.domain.exception;

public class UserInactiveException extends RuntimeException {

    public UserInactiveException(final String userId) {
        super(String.format("User with id=%s is in an inactive state", userId));
    }

    public UserInactiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
