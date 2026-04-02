package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationTest {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "secret";

    @Mock
    private UserService userService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private Authentication authentication;

    @Test
    void returnsTrueWhenPasswordMatches() {
        User user = new User();
        user.setPassword(PASSWORD);
        when(userService.getUser(USERNAME)).thenReturn(Optional.of(user));
        when(meterRegistry.counter("crm_auth_attempts_total", "result", "success")).thenReturn(counter);

        assertTrue(authentication.auth(USERNAME, PASSWORD));
        verify(counter).increment();
    }

    @Test
    void returnsFalseWhenPasswordDoesNotMatch() {
        User user = new User();
        user.setPassword("other");
        when(userService.getUser(USERNAME)).thenReturn(Optional.of(user));
        when(meterRegistry.counter("crm_auth_attempts_total", "result", "failure")).thenReturn(counter);

        assertThrows(AuthenticationFailedException.class, () -> authentication.auth(USERNAME, PASSWORD));
        verify(counter).increment();
    }

    @Test
    void throwsWhenUserNotFound() {
        when(userService.getUser(USERNAME)).thenReturn(Optional.empty());
        when(meterRegistry.counter("crm_auth_attempts_total", "result", "failure")).thenReturn(counter);

        assertThrows(AuthenticationFailedException.class, () -> authentication.auth(USERNAME, PASSWORD));
        verify(counter).increment();
    }
}
