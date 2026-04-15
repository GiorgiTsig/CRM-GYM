package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutSuccessHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutSuccessHandler logoutSuccessHandler;

    @Test
    void updatesLastLogoutAndReturnsOkWhenUserExists() throws Exception {
        User user = new User();
        when(authentication.getName()).thenReturn("john");
        when(userRepository.getUsersByUsername("john")).thenReturn(Optional.of(user));

        MockHttpServletResponse response = new MockHttpServletResponse();
        Instant before = Instant.now();
        logoutSuccessHandler.onLogoutSuccess(new MockHttpServletRequest(), response, authentication);
        Instant after = Instant.now();

        assertEquals(200, response.getStatus());
        assertNotNull(user.getLastLogout());
        assertFalse(user.getLastLogout().isBefore(before));
        assertFalse(user.getLastLogout().isAfter(after.plusSeconds(1)));
        verify(userRepository).save(user);
    }

    @Test
    void returnsOkAndSkipsRepositoryWhenAuthenticationIsNull() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        logoutSuccessHandler.onLogoutSuccess(new MockHttpServletRequest(), response, null);

        assertEquals(200, response.getStatus());
        verifyNoInteractions(userRepository);
    }

    @Test
    void returnsOkWithoutSavingWhenUserDoesNotExist() throws Exception {
        when(authentication.getName()).thenReturn("ghost");
        when(userRepository.getUsersByUsername("ghost")).thenReturn(Optional.empty());

        MockHttpServletResponse response = new MockHttpServletResponse();
        logoutSuccessHandler.onLogoutSuccess(new MockHttpServletRequest(), response, authentication);

        assertEquals(200, response.getStatus());
        verify(userRepository).getUsersByUsername("ghost");
        verify(userRepository, never()).save(any());
    }
}

