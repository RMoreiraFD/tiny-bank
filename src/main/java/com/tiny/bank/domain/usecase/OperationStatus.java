package com.tiny.bank.domain.usecase;

import java.util.Objects;

public record OperationStatus(boolean isSuccessful, String errorMessage) {

    public static OperationStatus success() {
        return new OperationStatus(true, null);
    }

    public static OperationStatus failure(String message) {
        Objects.requireNonNull(message, "Message shouldn't be null");

        return new OperationStatus(false, message);
    }

    public boolean isFailure() {
        return !isSuccessful;
    }
}
