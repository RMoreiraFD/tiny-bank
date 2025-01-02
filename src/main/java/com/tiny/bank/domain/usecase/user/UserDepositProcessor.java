package com.tiny.bank.domain.usecase.user;

import com.tiny.bank.domain.usecase.OperationStatus;
import com.tiny.bank.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class UserDepositProcessor {
    private final UserRepository repository;

    public UserDepositProcessor(final UserRepository repository) {
        this.repository = repository;
    }

    public OperationStatus process(final String ccNumber, final UUID accountId, final BigDecimal amount) {
        Objects.requireNonNull(ccNumber, "ccNumber shouldn't be null");
        Objects.requireNonNull(accountId, "accountId shouldn't be null");
        Objects.requireNonNull(amount, "amount shouldn't be null");

        //Since the account object is subject to the concept of mutability, there is no need to update the state of the map
        return repository.getUser(ccNumber)
                .map((user) -> {
                    user.deposit(accountId, amount);
                    return OperationStatus.success();
                })
                .orElse(OperationStatus.failure(String.format("User with ccNumber=%s doesn't exist", ccNumber)));
    }

}
