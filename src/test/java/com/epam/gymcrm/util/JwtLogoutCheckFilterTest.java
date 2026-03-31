package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.UserService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtLogoutCheckFilterTest {

    @Mock
    private UserService userService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtLogoutCheckFilter filter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allowsRequestWhenNoAuthenticationIsPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userService);
    }

    @Test
    void allowsRequestWhenPrincipalIsNotJwt() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john", "credentials")
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userService);
    }

    @Test
    void blocksRequestWhenTokenWasRevokedByLogout() throws Exception {
        Instant issuedAt = Instant.now().minusSeconds(120);
        User user = new User();
        user.setLastLogout(Instant.now().minusSeconds(30));

        SecurityContextHolder.getContext().setAuthentication(jwtAuthentication("john", issuedAt));
        when(userService.getUser("john")).thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertTrue(response.getContentAsString().contains("Token has been revoked via logout"));
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void allowsRequestWhenTokenIsIssuedAfterLastLogout() throws Exception {
        Instant issuedAt = Instant.now().minusSeconds(10);
        User user = new User();
        user.setLastLogout(Instant.now().minusSeconds(60));

        SecurityContextHolder.getContext().setAuthentication(jwtAuthentication("john", issuedAt));
        when(userService.getUser("john")).thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void throwsWhenAuthenticatedJwtUserCannotBeFound() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(jwtAuthentication("ghost", Instant.now()));
        when(userService.getUser("ghost")).thenReturn(Optional.empty());

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(UsernameNotFoundException.class, () -> filter.doFilter(request, response, filterChain));
        verify(filterChain, never()).doFilter(any(), any());
    }

    private Authentication jwtAuthentication(String subject, Instant issuedAt) {
        Jwt jwt = new Jwt(
                "token",
                issuedAt,
                issuedAt.plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", subject)
        );
        return new UsernamePasswordAuthenticationToken(jwt, null);
    }
}

