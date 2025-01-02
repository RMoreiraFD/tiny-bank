package com.tiny.bank.api.controller;

import com.tiny.bank.api.model.response.AccountBalanceResponse;
import com.tiny.bank.api.model.response.ErrorResponse;
import com.tiny.bank.api.model.response.GenericResponse;
import com.tiny.bank.api.model.response.SuccessResponse;
import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.usecase.OperationStatus;
import com.tiny.bank.domain.usecase.account.AccountBalanceViewer;
import com.tiny.bank.domain.usecase.account.AccountCreator;
import com.tiny.bank.domain.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    private final AccountBalanceViewer balanceViewer;
    private final AccountCreator accountCreator;

    public AccountController(final AccountBalanceViewer balanceViewer, final AccountCreator accountCreator) {
        this.balanceViewer = balanceViewer;
        this.accountCreator = accountCreator;
    }

    @PostMapping("/users/{userId}/accounts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> createAccount(@PathVariable final String userId) {

        final User user = accountCreator.create(userId);

        return ResponseEntity.ok().body(user);
    }


    @GetMapping("/users/{userId}/accounts/{accountId}/balance")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccountBalanceResponse> getBalance(@PathVariable final String userId,
                                                             @PathVariable final String accountId) {

        final Account account = balanceViewer.view(userId, UUID.fromString(accountId));

        return ResponseEntity.ok().body(new AccountBalanceResponse(account.getBalance(), account.getAccountId().toString(), userId));
    }

    @GetMapping("/users/{userId}/accounts/balance")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Set<AccountBalanceResponse>> getBalance(@PathVariable final String userId) {

        final Set<Account> accounts = balanceViewer.viewAllAccounts(userId);

        return ResponseEntity.ok().body(accounts.stream()
                .map(account -> new AccountBalanceResponse(account.getBalance(), account.getAccountId().toString(), userId))
                .collect(Collectors.toSet())
        );
    }

}
