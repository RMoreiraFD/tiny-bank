package com.tiny.bank.domain.usecase.user;

import com.tiny.bank.domain.exception.UserNotAvailableException;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserDeactivationProcessorTest {

    @Mock
    private UserRepository repositoryMock;

    private UserDeactivationProcessor victim;

    @BeforeEach
    void setUp() {
        openMocks(this);

        victim = new UserDeactivationProcessor(repositoryMock);
    }

    @Test
    void shouldTestTheUserDeactivation() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());

        when(repositoryMock.updateUser(eq("ccNumber"), any())).thenReturn(Optional.of(User.deactivateUser(user)));

        var result = victim.process("ccNumber");

        Assertions.assertThat(result.isUserDeactivated())
                .isTrue();
    }


    @Test
    void shouldTestTheUserDeactivationWhenUserDoesntExist() {
        when(repositoryMock.updateUser(anyString(), any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy( () -> victim.process("ccNumber"))
                        .isInstanceOf(UserNotAvailableException.class);
    }

}
