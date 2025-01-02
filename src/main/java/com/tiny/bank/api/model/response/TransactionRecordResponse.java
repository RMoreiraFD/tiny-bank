package com.tiny.bank.api.model.response;

import com.tiny.bank.domain.transaction.TransactionRecord;

import java.util.Set;

//Should convert the transactions records to and API model, but won't do it for the sake of simplicity
public record TransactionRecordResponse(Set<TransactionRecord> transactionRecords) {
}
