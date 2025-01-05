package com.tiny.bank.domain.account;

import com.google.common.collect.ImmutableSet;
import com.tiny.bank.domain.exception.InsufficientFundsException;
import com.tiny.bank.domain.transaction.InboundTransactionRecord;
import com.tiny.bank.domain.transaction.OutboundTransactionRecord;
import com.tiny.bank.domain.transaction.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a mutable thread-safe bank account.
 *
 * <p>
 * Provides functionality for deposits, withdrawals, and transfers while ensuring
 * data integrity in concurrent environments using a {@link ReentrantReadWriteLock}.
 * </p>
 */
public class Account {

    private static final Logger LOGGER = LoggerFactory.getLogger(Account.class);

    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(true);
    private final UUID accountId;
    private final Set<TransactionRecord> transactions;

    private BigDecimal balance;

    public Account() {
        this.accountId = UUID.randomUUID();
        this.transactions = Set.of();
    }

    /**
     * Constructs an {@code Account} with the specified parameters.
     *
     * @param accountId    the unique identifier of the account
     * @param transactions the set of transactions associated with the account
     * @param balance      the initial balance of the account
     */
    public Account(final UUID accountId, final Set<TransactionRecord> transactions, final BigDecimal balance) {
        Objects.requireNonNull(accountId, "accountId shouldn't be null");
        Objects.requireNonNull(transactions, "transactions shouldn't be null");
        Objects.requireNonNull(balance, "balance shouldn't be null");

        this.accountId = accountId;
        this.transactions = transactions;
        this.balance = balance;
    }

    /**
     * Creates a new account with a unique identifier and an initial balance of {@link BigDecimal#ZERO}.
     *
     * @return a new {@code Account} instance.
     */
    public static Account createAccount() {
        return new Account(UUID.randomUUID(), ConcurrentHashMap.newKeySet(), BigDecimal.ZERO);
    }

    /**
     * Creates a new account with a unique identifier and an initial balance.
     *
     * @param balance the initial balance of the account.
     * @return a new {@code Account} instance.
     */
    public static Account createAccountWithInitialBalance(final BigDecimal balance) {
        return new Account(UUID.randomUUID(), ConcurrentHashMap.newKeySet(), balance);
    }

    /**
     * Returns the unique identifier of the account.
     *
     * @return the account's UUID.
     */
    public UUID getAccountId() {
        return accountId;
    }

    /**
     * Returns an immutable view of the account's transaction records.
     *
     * @return a {@link Set} of {@link TransactionRecord}
     */
    public Set<TransactionRecord> getTransactions() {
        return ImmutableSet.copyOf(transactions);
    }

    /**
     * Returns the current balance of the account.
     *
     * @return the account's balance.
     */
    public BigDecimal getBalance() {
        final ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();

        try {
            readLock.lock();
            return balance;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Performs a withdrawal from the account and logs the transaction.
     *
     * @param amount the amount to withdraw.
     * @return the account balance after the withdrawal.
     * @throws InsufficientFundsException if the account has insufficient funds for the transaction.
     * @throws IllegalArgumentException   if the amount is negative or zero.
     */
    public BigDecimal bankWithdrawal(final BigDecimal amount) {
        final BigDecimal balance = withdraw(amount);

        OutboundTransactionRecord record = OutboundTransactionRecord.createPersonalOutboundTransaction(amount, balance, accountId);

        transactions.add(record);

        return balance;
    }

    /**
     * Deposits an amount into the account and logs the transaction.
     *
     * @param amount the amount to deposit.
     * @return the account balance after the withdrawal.
     * @throws IllegalArgumentException if the amount is negative or zero.
     */
    public BigDecimal bankDeposit(final BigDecimal amount) {
        final BigDecimal balance = deposit(amount);

        final InboundTransactionRecord record = InboundTransactionRecord.createPersonalInboundTransaction(amount, balance, accountId);

        transactions.add(record);

        return balance;
    }

    /**
     * Transfers an amount from this account to the recipient account.
     *
     * @param amount    the amount to transfer.
     * @param recipient the recipient {@code Account}.
     * @throws IllegalArgumentException   if the amount is negative or zero.
     * @throws InsufficientFundsException if the account has insufficient funds for the transfer.
     */
    public void transferTo(final BigDecimal amount, final Account recipient) {
        Objects.requireNonNull(amount, "Amount shouldn't be null");
        Objects.requireNonNull(recipient, "Recipient shouldn't be null");

        if (this.equals(recipient)) {
            throw new IllegalArgumentException("Attempting to transfer within the same account");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The amount being deposited is lower or equal to 0");
        }

        final Account first = this.hashCode() < recipient.hashCode() ? this : recipient;
        final Account second = first == this ? recipient : this;

        try {
            first.reentrantReadWriteLock.writeLock().lock();
            second.reentrantReadWriteLock.writeLock().lock();

            this.withdraw(amount);
            recipient.deposit(amount);

            //Could be cleaner
            Set<TransactionRecord> transactionRecords = createTransactionRecord(amount, recipient);

            transactions.addAll(transactionRecords.stream().filter(x -> x instanceof OutboundTransactionRecord).toList());
            recipient.transactions.addAll(transactionRecords.stream().filter(x -> x instanceof InboundTransactionRecord).toList());
        } finally {
            first.reentrantReadWriteLock.writeLock().unlock();
            second.reentrantReadWriteLock.writeLock().unlock();
        }
    }

    private BigDecimal withdraw(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount shouldn't be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The amount being withdrawn is lower or equal to 0");
        }

        final ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();

        if (balance.subtract(amount).compareTo(BigDecimal.ZERO) < 0 && !reentrantReadWriteLock.isWriteLocked()) {
            LOGGER.error("operation=withdraw, message=Insufficient funds to process transaction, balance={}, amount={}",
                    balance, amount);
            throw new InsufficientFundsException(String.format("Current balance=%s is insufficient to process the transaction", balance));
        }

        try {
            writeLock.lock();

            if (balance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
                LOGGER.error("operation=withdraw, message=Insufficient funds to process transaction, balance={}, amount={}",
                        balance, amount);
                throw new InsufficientFundsException(String.format("Current balance=%s is insufficient to process the transaction", balance));
            }

            balance = balance.subtract(amount);
            return balance;
        } finally {
            writeLock.unlock();
        }
    }

    private BigDecimal deposit(final BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount shouldn't be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The amount being deposited is lower or equal to 0");
        }

        final ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();

        try {
            writeLock.lock();

            balance = balance.add(amount);
            return balance;
        } finally {
            writeLock.unlock();
        }
    }

    private Set<TransactionRecord> createTransactionRecord(final BigDecimal amount, final Account recipient) {
        final String description = String.format("Transaction from account %s to account %s", this.accountId, recipient.accountId);

        final LocalDateTime transactionDate = LocalDateTime.now();
        final UUID transactionID = UUID.randomUUID();

        final TransactionRecord inboundRecord = new InboundTransactionRecord(transactionID,
                amount,
                recipient.balance,
                transactionDate,
                description,
                this,
                this.accountId);

        final TransactionRecord outboundRecord = new OutboundTransactionRecord(transactionID,
                amount,
                this.balance,
                transactionDate,
                description,
                recipient,
                this.accountId);

        return Set.of(inboundRecord, outboundRecord);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }

}
