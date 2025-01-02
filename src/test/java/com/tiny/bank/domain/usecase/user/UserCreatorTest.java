package com.tiny.bank.domain.usecase.user;

import com.tiny.bank.domain.exception.UserAlreadyExistsException;
import com.tiny.bank.domain.user.User;
import com.tiny.bank.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserCreatorTest {

    @Mock
    private UserRepository repositoryMock;

    private UserCreator victim;

    @BeforeEach
    void setUp() {
        openMocks(this);

        victim = new UserCreator(repositoryMock);
    }

    @Test
    void shouldTestTheCreationOfAnUser() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());

        when(repositoryMock.addUser(user)).thenReturn(true);

        var result = victim.create(user);

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(result)
                .isEqualTo(user);
    }

    @Test
    void shouldTestTheCreationOfAnUserFailsIfUserAlreadyExists() {
        var user = User.createUser("name", "ccNumber", LocalDate.now());

        when(repositoryMock.addUser(user)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> victim.create(user))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

}
