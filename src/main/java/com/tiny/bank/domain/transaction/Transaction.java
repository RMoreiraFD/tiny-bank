package com.tiny.bank.domain.transaction;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record Transaction(BigDecimal amount,
                          String providerId,
                          UUID providerAccountId,
                          String recipientId,
                          UUID recipientAccountId) {
    public Transaction {
        Objects.requireNonNull(amount, "amount shouldn't be null");

        Objects.requireNonNull(providerId, "providerId shouldn't be null");

        Objects.requireNonNull(providerAccountId, "providerAccountId shouldn't be null");

        Objects.requireNonNull(recipientId, "recipientId shouldn't be null");

        Objects.requireNonNull(recipientAccountId, "recipientAccountId shouldn't be null");
    }

    public boolean isTransactionBetweenUserAccounts() {
        return providerAccountId.equals(recipientAccountId);
    }

}
