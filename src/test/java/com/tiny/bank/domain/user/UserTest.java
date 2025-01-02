package com.tiny.bank.domain.user;

import com.tiny.bank.domain.account.Account;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

class UserTest {

    @Test
    void shouldTestUserDeactivation() {
        var victim = User.createUser("name", "ccNumber", LocalDate.now());

        Assertions.assertThat(victim.isUserDeactivated())
                .isFalse();

        var deactivateVictim = User.deactivateUser(victim);

        Assertions.assertThat(deactivateVictim.isUserDeactivated())
                .isTrue();
    }

    @Test
    void shouldTestAccountAdditionToUser() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());

        var victim = User.createAccount(user);

        Assertions.assertThat(victim.accounts())
                .isNotEmpty();
    }

    @Test
    void shouldTestDepositWhenUserIsInactive() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());

        var victim = User.deactivateUser(user);

        Assertions.assertThatThrownBy(() -> victim.deposit(UUID.randomUUID(), BigDecimal.TEN));
    }

    @Test
    void shouldTestDeposit() {
        var account = Account.createAccount();
        var victim = new User(UUID.randomUUID(), "name", "ccNumber", LocalDate.now(), Set.of(account), State.ACTIVE);

        var result = victim.deposit(account.getAccountId(), BigDecimal.TEN);

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void shouldTestWithdrawalWhenUserIsInactive() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());

        var victim = User.deactivateUser(user);

        Assertions.assertThatThrownBy(() -> victim.withdraw(UUID.randomUUID(), BigDecimal.TEN));
    }

    @Test
    void shouldTestWithdrawal() {
        var account = Account.createAccountWithInitialBalance(BigDecimal.valueOf(150.0));
        var victim = new User(UUID.randomUUID(), "name", "ccNumber", LocalDate.now(), Set.of(account), State.ACTIVE);

        var result = victim.withdraw(account.getAccountId(), BigDecimal.TEN);

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .isEqualTo(BigDecimal.valueOf(140.0));
    }

    @Test
    void shouldTestFindAccount() {
        var account = Account.createAccount();
        var victim = new User(UUID.randomUUID(), "name", "ccNumber", LocalDate.now(), Set.of(account), State.ACTIVE);

        var result = victim.findAccount(account.getAccountId());

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .isEqualTo(account);
    }

    @Test
    void shouldTestFindAccountFails() {
        var account = Account.createAccount();
        var victim = new User(UUID.randomUUID(), "name", "ccNumber", LocalDate.now(), Set.of(account), State.ACTIVE);

        Assertions.assertThatThrownBy(() -> victim.findAccount(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}