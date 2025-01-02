package com.tiny.bank.domain.transaction;

public enum TransactionType {
    INBOUND("+"),
    OUTBOUND("-");

    private final String signal;

    TransactionType(String signal) {
        this.signal = signal;
    }

    public String getSignal() {
        return signal;
    }
}
