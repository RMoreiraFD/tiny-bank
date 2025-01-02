package com.tiny.bank.domain.usecase.user;

import com.tiny.bank.domain.usecase.OperationStatus;
import com.tiny.bank.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class UserWithdrawalProcessor {
    private final UserRepository repository;

    public UserWithdrawalProcessor(final UserRepository repository) {
        this.repository = repository;
    }

    public OperationStatus process(final String ccNumber, final UUID accountId, final BigDecimal amount) {
        Objects.requireNonNull(ccNumber, "ccNumber shouldn't be null");
        Objects.requireNonNull(accountId, "accountId shouldn't be null");
        Objects.requireNonNull(amount, "amount shouldn't be null");

        //Since the account object is subject to the concept of mutability, there is no need to update the state of the map
        return repository.getUser(ccNumber)
                .map((user) -> {
                    user.withdraw(accountId, amount);
                    return OperationStatus.success();
                })
                .orElse(OperationStatus.failure(String.format("User with ccNumber=%s doesn't exist", ccNumber)));
    }

}
