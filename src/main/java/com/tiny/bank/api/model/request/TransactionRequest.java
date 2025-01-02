package com.tiny.bank.api.model.request;

public record TransactionRequest(String senderId,
                                 String senderAccountId,
                                 String recipientId,
                                 String recipientAccountId,
                                 Double amount) {
}
