package com.tiny.bank.domain.account;

import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.exception.InsufficientFundsException;
import com.tiny.bank.domain.transaction.InboundTransactionRecord;
import com.tiny.bank.domain.transaction.OutboundTransactionRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class AccountTest {

    @Test
    void shouldTestTheFailureOfWithdrawalActionWhenFundsAreInsufficient() {
        final Account victim = Account.createAccount();

        Assertions.assertThatThrownBy(() -> victim.bankWithdrawal(BigDecimal.valueOf(150.0)))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void shouldTestADepositAction() {
        final Account victim = Account.createAccount();

        var balance = victim.bankDeposit(BigDecimal.valueOf(150.0));
        var transactions = victim.getTransactions();

        Assertions.assertThat(balance)
                .isNotNull();

        Assertions.assertThat(balance)
                .isEqualTo(BigDecimal.valueOf(150.0));

        Assertions.assertThat(transactions)
                .isNotNull();

        Assertions.assertThat(transactions)
                .isNotEmpty();

        Assertions.assertThat(transactions)
                .hasOnlyElementsOfType(InboundTransactionRecord.class);

        Assertions.assertThat(transactions)
                .extracting("amount")
                .isEqualTo(List.of(BigDecimal.valueOf(150.0)));
    }

    @Test
    void shouldTestAWithdrawalAction() {
        var victim = Account.createAccountWithInitialBalance(BigDecimal.valueOf(150.0));

        var balance = victim.bankWithdrawal(BigDecimal.valueOf(150.0));
        var transactions = victim.getTransactions();

        Assertions.assertThat(balance)
                .isNotNull();

        Assertions.assertThat(balance)
                .isEqualTo(BigDecimal.valueOf(0.0));

        Assertions.assertThat(transactions)
                .isNotNull();

        Assertions.assertThat(transactions)
                .isNotEmpty();

        Assertions.assertThat(transactions)
                .hasOnlyElementsOfType(OutboundTransactionRecord.class);

        Assertions.assertThat(transactions)
                .extracting("amount")
                .isEqualTo(List.of(BigDecimal.valueOf(150.0)));
    }

    @Test
    void shouldTestAWithdrawalFailure() {
        var victim = Account.createAccountWithInitialBalance(BigDecimal.valueOf(140.0));

        var transactions = victim.getTransactions();

        Assertions.assertThatThrownBy(() -> victim.bankWithdrawal(BigDecimal.valueOf(150.0)))
                .isInstanceOf(InsufficientFundsException.class);

        Assertions.assertThat(victim.getBalance())
                .isEqualTo(BigDecimal.valueOf(140.0));

        Assertions.assertThat(transactions)
                .isNotNull();

        Assertions.assertThat(transactions)
                .isEmpty();
    }

    @Test
    void shouldTestATransactionAction() {
        var recipient = Account.createAccount();
        var sender = Account.createAccountWithInitialBalance(BigDecimal.valueOf(150.0));

        sender.transferTo(BigDecimal.TEN, recipient);

        Assertions.assertThat(recipient.getBalance())
                .isNotNull();

        Assertions.assertThat(recipient.getBalance())
                .isEqualTo(BigDecimal.TEN);

        Assertions.assertThat(recipient.getTransactions())
                .isNotNull();

        Assertions.assertThat(recipient.getTransactions())
                .isNotEmpty();

        Assertions.assertThat(recipient.getTransactions())
                .hasOnlyElementsOfType(InboundTransactionRecord.class);

        Assertions.assertThat(recipient.getTransactions())
                .extracting("amount")
                .isEqualTo(List.of(BigDecimal.TEN));

        Assertions.assertThat(sender.getBalance())
                .isNotNull();

        Assertions.assertThat(sender.getBalance())
                .isEqualTo(BigDecimal.valueOf(140.0));

        Assertions.assertThat(sender.getTransactions())
                .isNotNull();

        Assertions.assertThat(sender.getTransactions())
                .isNotEmpty();

        Assertions.assertThat(sender.getTransactions())
                .hasOnlyElementsOfType(OutboundTransactionRecord.class);

        Assertions.assertThat(sender.getTransactions())
                .extracting("amount")
                .isEqualTo(List.of(BigDecimal.TEN));
    }

    @Test
    void shouldTestATransactionFailureWithInsufficientFundsFromSender() {
        var recipient = Account.createAccount();
        var sender = Account.createAccountWithInitialBalance(BigDecimal.valueOf(9.0));

        Assertions.assertThatThrownBy(() -> sender.transferTo(BigDecimal.TEN, recipient))
                        .isInstanceOf(InsufficientFundsException.class);

        Assertions.assertThat(recipient.getBalance())
                .isNotNull();

        Assertions.assertThat(recipient.getBalance())
                .isEqualTo(BigDecimal.ZERO);

        Assertions.assertThat(recipient.getTransactions())
                .isNotNull();

        Assertions.assertThat(recipient.getTransactions())
                .isEmpty();

        Assertions.assertThat(sender.getBalance())
                .isNotNull();

        Assertions.assertThat(sender.getBalance())
                .isEqualTo(BigDecimal.valueOf(9.0));

        Assertions.assertThat(sender.getTransactions())
                .isNotNull();

        Assertions.assertThat(sender.getTransactions())
                .isEmpty();
    }

}
