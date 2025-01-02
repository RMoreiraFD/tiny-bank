package com.tiny.bank.repository;

import com.tiny.bank.domain.user.User;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Repository class for managing {@link User} entities.
 */
public class UserRepository {

    private final ConcurrentHashMap<String, User> repository;

    /**
     * Constructs a new instance of {@code UserRepository}.
     * Initializes the repository with a thread-safe {@link ConcurrentHashMap}.
     */
    public UserRepository() {
        this.repository = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves a user from the repository by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return an {@link Optional} containing the {@link User} if found, or an empty {@link Optional} if not found.
     */
    public Optional<User> getUser(final String id) {
        return Optional.ofNullable(repository.get(id));
    }

    /**
     * Adds a new user to the repository.
     *
     * <p>
     * If a user with the same credit card number already exists, the operation fails.
     * </p>
     *
     * @param user the {@link User} to add.
     * @return {@code true} if the user was successfully added, {@code false} if user was already present.
     */
    public boolean addUser(final User user) {
        return repository.putIfAbsent(user.ccNumber(), user) == null;
    }

    /**
     * Updates an existing user in the repository using the provided update function.
     *
     * <p>
     * If no user with the specified credit card number exists, the operation does nothing. The update operation
     * is atomic.
     * </p>
     *
     * @param ccNumber     the credit card number of the user to update.
     * @param userFunction a {@link Function} that defines the update logic.
     * @return an {@link Optional} containing the updated {@link User} if the update was successful,
     * or an empty {@link Optional} if no user with the given credit card number exists.
     */
    public Optional<User> updateUser(final String ccNumber, final Function<User, User> userFunction) {
        return Optional.ofNullable(repository.computeIfPresent(ccNumber, (key, user) -> userFunction.apply(user)));
    }
}
