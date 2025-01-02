package com.tiny.bank.repository;

import com.tiny.bank.domain.account.Account;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepository {

    private final ConcurrentHashMap<UUID, Account> repository;

    public AccountRepository() {
        this.repository = new ConcurrentHashMap<>();
    }

    public Optional<Account> getAccount(final UUID id) {
        return Optional.ofNullable(repository.get(id));
    }

    public Set<Account> getAccounts() {
        return new HashSet<>(repository.values());
    }

    public Account addAccount(final Account account) {
        return repository.putIfAbsent(account.getAccountId(), account);
    }

    public Account updateAccount(final Account account) {
        return repository.put(account.getAccountId(), account);
    }

    public boolean containsAccount(final UUID id) {
        return repository.containsKey(id);
    }
}
