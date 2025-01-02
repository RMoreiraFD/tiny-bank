package com.tiny.bank.config;

import com.tiny.bank.domain.usecase.account.AccountBalanceViewer;
import com.tiny.bank.domain.usecase.account.AccountCreator;
import com.tiny.bank.domain.usecase.transaction.TransactionHistoryViewer;
import com.tiny.bank.domain.usecase.transaction.TransactionProcessor;
import com.tiny.bank.domain.usecase.user.UserCreator;
import com.tiny.bank.domain.usecase.user.UserDeactivationProcessor;
import com.tiny.bank.domain.usecase.user.UserDepositProcessor;
import com.tiny.bank.domain.usecase.user.UserWithdrawalProcessor;
import com.tiny.bank.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    UserCreator userCreator(final UserRepository repository) {
        return new UserCreator(repository);
    }

    @Bean
    UserDeactivationProcessor userDeactivationProcessor(final UserRepository repository) {
        return new UserDeactivationProcessor(repository);
    }

    @Bean
    UserDepositProcessor userDepositProcessor(final UserRepository repository) {
        return new UserDepositProcessor(repository);
    }

    @Bean
    UserWithdrawalProcessor userWithdrawalProcessor(final UserRepository repository) {
        return new UserWithdrawalProcessor(repository);
    }

    @Bean
    TransactionProcessor transactionProcessor(final UserRepository repository) {
        return new TransactionProcessor(repository);
    }

    @Bean
    TransactionHistoryViewer transactionHistoryViewer(final UserRepository repository) {
        return new TransactionHistoryViewer(repository);
    }

    @Bean
    AccountBalanceViewer accountBalanceViewer(final UserRepository repository) {
        return new AccountBalanceViewer(repository);
    }

    @Bean
    AccountCreator accountCreator(final UserRepository repository) {
        return new AccountCreator(repository);
    }

}
