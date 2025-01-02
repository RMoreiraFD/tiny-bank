package com.tiny.bank.api.controller;

import com.tiny.bank.api.model.request.TransactionRequest;
import com.tiny.bank.api.model.response.ErrorResponse;
import com.tiny.bank.api.model.response.GenericResponse;
import com.tiny.bank.api.model.response.SuccessResponse;
import com.tiny.bank.api.model.response.TransactionRecordResponse;
import com.tiny.bank.domain.transaction.Transaction;
import com.tiny.bank.domain.transaction.TransactionRecord;
import com.tiny.bank.domain.usecase.OperationStatus;
import com.tiny.bank.domain.usecase.transaction.TransactionHistoryViewer;
import com.tiny.bank.domain.usecase.transaction.TransactionProcessor;
import com.tiny.bank.domain.usecase.user.UserDepositProcessor;
import com.tiny.bank.domain.usecase.user.UserWithdrawalProcessor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {

    private final UserDepositProcessor depositProcessor;
    private final UserWithdrawalProcessor userWithdrawalProcessor;
    private final TransactionHistoryViewer transactionHistoryViewer;
    private final TransactionProcessor transactionProcessor;

    public TransactionController(final UserDepositProcessor depositProcessor,
                                 final UserWithdrawalProcessor userWithdrawalProcessor,
                                 final TransactionHistoryViewer transactionHistoryViewer,
                                 final TransactionProcessor transactionProcessor) {
        this.depositProcessor = depositProcessor;
        this.userWithdrawalProcessor = userWithdrawalProcessor;
        this.transactionHistoryViewer = transactionHistoryViewer;
        this.transactionProcessor = transactionProcessor;
    }

    @PostMapping("users/{userId}/accounts/{accountId}/deposit")
    public ResponseEntity<GenericResponse> deposit(@PathVariable final String userId,
                                                   @PathVariable final String accountId,
                                                   @RequestParam final Double amount) {

        final OperationStatus status = depositProcessor.process(userId, UUID.fromString(accountId), BigDecimal.valueOf(amount));

        if (status.isFailure()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(status.errorMessage()));
        }

        return ResponseEntity.ok().body(new SuccessResponse("Operation successful"));
    }

    @PostMapping("users/{userId}/accounts/{accountId}/withdraw")
    public ResponseEntity<GenericResponse> withdraw(@PathVariable final String userId,
                                                    @PathVariable final String accountId,
                                                    @RequestParam final Double amount) {

        final OperationStatus status = userWithdrawalProcessor.process(userId,
                UUID.fromString(accountId),
                BigDecimal.valueOf(amount));

        if (status.isFailure()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(status.errorMessage()));
        }

        return ResponseEntity.ok().body(new SuccessResponse("Operation successful"));
    }

    @GetMapping("users/{userId}/accounts/{accountId}/transactions")
    public ResponseEntity<TransactionRecordResponse> accountTransactionHistory(@PathVariable final String userId,
                                                                               @PathVariable final String accountId) {
        final Set<TransactionRecord> transactionRecords = transactionHistoryViewer.view(userId, UUID.fromString(accountId));

        return ResponseEntity.ok().body(new TransactionRecordResponse(transactionRecords));
    }

    @GetMapping("users/{userId}/transactions")
    public ResponseEntity<Set<TransactionRecordResponse>> accountTransactionHistory(@PathVariable final String userId) {
        return ResponseEntity.ok().body(transactionHistoryViewer.viewAllUserTransactions(userId)
                .stream()
                .map(TransactionRecordResponse::new)
                .collect(Collectors.toSet())
        );
    }

    @PostMapping("transaction")
    public ResponseEntity<Void> processTransaction(@RequestBody final TransactionRequest body) {
        final Transaction transaction = new Transaction(BigDecimal.valueOf(body.amount()),
                body.senderId(),
                UUID.fromString(body.senderAccountId()),
                body.recipientId(),
                UUID.fromString(body.recipientAccountId()));

        transactionProcessor.process(transaction);

        return ResponseEntity.ok().build();
    }

}
