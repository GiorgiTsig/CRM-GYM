package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationTest {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "secret";

    @Mock
    private UserService userService;

    @InjectMocks
    private Authentication authentication;

    @Test
    void returnsTrueWhenPasswordMatches() {
        User user = new User();
        user.setPassword(PASSWORD);
        when(userService.getUser(USERNAME)).thenReturn(Optional.of(user));

        assertTrue(authentication.auth(USERNAME, PASSWORD));
    }

    @Test
    void returnsFalseWhenPasswordDoesNotMatch() {
        User user = new User();
        user.setPassword("other");
        when(userService.getUser(USERNAME)).thenReturn(Optional.of(user));

        assertThrows(AuthenticationFailedException.class, () -> authentication.auth(USERNAME, PASSWORD));
    }

    @Test
    void throwsWhenUserNotFound() {
        when(userService.getUser(USERNAME)).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class, () -> authentication.auth(USERNAME, PASSWORD));
    }
}
