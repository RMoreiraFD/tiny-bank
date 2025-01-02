package com.tiny.bank.domain.transaction;

import com.google.common.base.MoreObjects;
import com.tiny.bank.domain.account.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OutboundTransactionRecord(UUID id,
                                        BigDecimal amount,
                                        BigDecimal balanceAfterTransaction,
                                        LocalDateTime date,
                                        String description,
                                        Account recipient,
                                        UUID accountId) implements TransactionRecord {

    public static OutboundTransactionRecord createPersonalOutboundTransaction(final BigDecimal amount,
                                                                              final BigDecimal finalBalance,
                                                                              final UUID accountId) {
        final String description = String.format("Bank personal withdraw, amount=%s, balance=%s", amount, finalBalance);

        return new OutboundTransactionRecord(UUID.randomUUID(),
                amount,
                finalBalance,
                LocalDateTime.now(),
                description,
                null,
                accountId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("amount", String.format("%s%s", TransactionType.OUTBOUND.getSignal(), amount))
                .add("balanceAfterTransaction", balanceAfterTransaction)
                .add("date", date)
                .add("description", description)
                .add("recipient", recipient == null ? "Withdrawal" : recipient.getAccountId())
                .add("accountId", accountId)
                .toString();
    }
}
