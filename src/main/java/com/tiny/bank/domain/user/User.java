package com.tiny.bank.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tiny.bank.domain.account.Account;
import com.tiny.bank.domain.exception.UserInactiveException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a user in the banking system.
 *
 * @param uuid      the unique identifier of the user.
 * @param name      the name of the user.
 * @param ccNumber  the credit card number of the user.
 * @param birthdate the birthdate of the user.
 * @param accounts  the set of accounts associated with the user.
 * @param state     the current state of the user (e.g., active or inactive).
 */
public record User(UUID uuid, String name, String ccNumber, LocalDate birthdate, Set<Account> accounts, State state) {

    public User {
        Objects.requireNonNull(uuid, "User id shouldn't be null");

        Objects.requireNonNull(name, "Name shouldn't be null");

        Objects.requireNonNull(ccNumber, "ccNumber shouldn't be null");

        Objects.requireNonNull(birthdate, "birthdate shouldn't be null");

        Objects.requireNonNull(accounts, "accounts shouldn't be null");
    }

    /**
     * Creates a new active user.
     *
     * @param name      the name of the user.
     * @param CCNumber  the credit card number of the user.
     * @param birthdate the birthdate of the user.
     * @return a new {@link User} instance with an empty account set and active state.
     */
    public static User createUser(final String name,
                                  final String CCNumber,
                                  final LocalDate birthdate) {
        return new User(UUID.randomUUID(), name, CCNumber, birthdate, Collections.emptySet(), State.ACTIVE);
    }

    /**
     * Deactivates the given user.
     *
     * <p>
     * If the user is already deactivated, no changes are made.
     * </p>
     *
     * @param user the {@link User} to deactivate.
     * @return a new {@link User} instance with the inactive state, or the original user if already deactivated.
     */
    public static User deactivateUser(final User user) {
        if (user.isUserDeactivated()) {
            return user;
        }

        return new User(user.uuid, user.name, user.ccNumber, user.birthdate, user.accounts, State.INACTIVE);
    }

    /**
     * Adds a new account to the user's list of accounts.
     *
     * @param user the {@link User} to which a new account will be added.
     * @return a new {@link User} instance with the updated account set.
     * @throws UserInactiveException if the user is in an inactive state.
     */
    public static User createAccount(final User user) {
        if (user.isUserDeactivated()) {
            throw new UserInactiveException(user.ccNumber);
        }

        final Set<Account> accounts = new HashSet<>(user.accounts);
        accounts.add(Account.createAccount());

        return new User(user.uuid, user.name, user.ccNumber, user.birthdate, accounts, user.state);
    }

    /**
     * Transfers a specified amount between two accounts owned by the user.
     *
     * @param amount             the amount to transfer.
     * @param providerAccountId  the UUID of the provider account.
     * @param recipientAccountId the UUID of the recipient account.
     * @throws IllegalArgumentException if any of the accounts are not found.
     * @throws UserInactiveException if the user is in an inactive state.
     */
    public void transferBetweenAccounts(final BigDecimal amount,
                                        final UUID providerAccountId,
                                        final UUID recipientAccountId) {
        if (isUserDeactivated()) {
            throw new UserInactiveException(this.ccNumber);
        }

        final Account provider = this.findAccount(providerAccountId);
        final Account recipient = this.findAccount(recipientAccountId);

        provider.transferTo(amount, recipient);
    }


    /**
     * Deposits a specified amount into a user's account.
     *
     * @param accountId the UUID of the account to deposit into.
     * @param amount    the amount to deposit.
     * @throws IllegalArgumentException if the account is not found.
     * @throws UserInactiveException if the user is in an inactive state.
     */
    public BigDecimal deposit(final UUID accountId, final BigDecimal amount) {
        if (isUserDeactivated()) {
            throw new UserInactiveException(this.ccNumber);
        }

        final Account account = findAccount(accountId);

        return account.bankDeposit(amount);
    }

    /**
     * Withdraws a specified amount from a user's account.
     *
     * @param accountId the UUID of the account to withdraw from.
     * @param amount    the amount to withdraw.
     * @throws IllegalArgumentException if the account is not found.
     * @throws UserInactiveException if the user is in an inactive state.
     */
    public BigDecimal withdraw(final UUID accountId, final BigDecimal amount) {
        if (isUserDeactivated()) {
            throw new UserInactiveException(this.ccNumber);
        }

        final Account account = findAccount(accountId);

        return account.bankWithdrawal(amount);
    }

    /**
     * Finds an account associated with the user by its UUID.
     *
     * @param accountId the UUID of the account to find.
     * @return the {@link Account} with the specified UUID.
     * @throws IllegalArgumentException if the account is not found.
     */
    public Account findAccount(final UUID accountId) {
        return this.accounts.stream()
                .filter(acc -> acc.getAccountId().equals(accountId))
                .limit(1)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Account with id=%s not found for user=%s", accountId, this.ccNumber))
                );
    }

    /**
     * Checks if the user is currently deactivated.
     *
     * @return {@code true} if the user's state is inactive, {@code false} otherwise.
     */
    @JsonIgnore
    public boolean isUserDeactivated() {
        return state == State.INACTIVE;
    }
}
