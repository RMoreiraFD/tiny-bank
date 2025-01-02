package com.tiny.bank.domain.transaction;

import com.google.common.base.MoreObjects;
import com.tiny.bank.domain.account.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InboundTransactionRecord(UUID id,
                                       BigDecimal amount,
                                       BigDecimal balanceAfterTransaction,
                                       LocalDateTime date,
                                       String description,
                                       Account provider,
                                       UUID accountId) implements TransactionRecord {

    public static InboundTransactionRecord createPersonalInboundTransaction(final BigDecimal amount,
                                                                            final BigDecimal finalBalance,
                                                                            final UUID accountId) {
        final String description = String.format("Bank personal deposit, amount=%s, balance=%s", amount, finalBalance);

        return new InboundTransactionRecord(UUID.randomUUID(),
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
                .add("amount", String.format("%s%s", TransactionType.INBOUND.getSignal(), amount))
                .add("balanceAfterTransaction", balanceAfterTransaction)
                .add("date", date)
                .add("description", description)
                .add("provider", provider == null ? "Deposit" : provider.getAccountId())
                .add("accountId", accountId)
                .toString();
    }
}
