package com.tiny.bank.domain.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface TransactionRecord permits InboundTransactionRecord, OutboundTransactionRecord {

    UUID id();

    BigDecimal amount();

    BigDecimal balanceAfterTransaction();

    LocalDateTime date();

    String description();

    UUID accountId();
}
