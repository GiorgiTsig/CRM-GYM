package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFailureListenerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationFailureListener listener;

    @Test
    void incrementsAttemptsWithoutLockingWhenBelowMaxThreshold() {
        User user = new User();
        user.setFailedLoginAttempts(1);
        when(userService.getUser("john")).thenReturn(Optional.of(user));

        listener.onApplicationEvent(eventFor("john"));

        assertEquals(2, user.getFailedLoginAttempts());
        assertNull(user.getLockUntil());
        verify(userRepository).save(user);
    }

    @Test
    void setsLockUntilWhenMaxAttemptsIsReached() {
        User user = new User();
        user.setFailedLoginAttempts(2);
        when(userService.getUser("john")).thenReturn(Optional.of(user));

        LocalDateTime before = LocalDateTime.now();
        listener.onApplicationEvent(eventFor("john"));
        LocalDateTime after = LocalDateTime.now();

        assertEquals(3, user.getFailedLoginAttempts());
        assertNotNull(user.getLockUntil());
        assertFalse(user.getLockUntil().isBefore(before.plusMinutes(5)));
        assertFalse(user.getLockUntil().isAfter(after.plusMinutes(5).plusSeconds(1)));
        verify(userRepository).save(user);
    }

    @Test
    void doesNotPersistAnythingWhenUserDoesNotExist() {
        when(userService.getUser("ghost")).thenReturn(Optional.empty());

        listener.onApplicationEvent(eventFor("ghost"));

        verify(userService).getUser("ghost");
        verify(userRepository, never()).save(any());
    }

    private AuthenticationFailureBadCredentialsEvent eventFor(String username) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, "wrong-password");
        return new AuthenticationFailureBadCredentialsEvent(authentication, new BadCredentialsException("invalid credentials"));
    }
}

