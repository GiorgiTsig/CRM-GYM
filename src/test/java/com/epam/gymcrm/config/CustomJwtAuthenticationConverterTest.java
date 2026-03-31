package com.epam.gymcrm.config;

import com.epam.gymcrm.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomJwtAuthenticationConverterTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Test
    void convertBuildsAuthenticatedPreAuthTokenWithJwtCredentialsAndAuthorities() {
        Jwt jwt = jwt("john", Map.of("scope", "user"));
        UserDetails userDetails = User.withUsername("john")
                .password("noop")
                .authorities("ROLE_USER")
                .build();
        when(customUserDetailsService.loadUserByUsername(jwt.getSubject())).thenReturn(userDetails);

        Authentication authentication = jwtAuthenticationConverter.convert(jwt);

        assertInstanceOf(PreAuthenticatedAuthenticationToken.class, authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        assertEquals(jwt, authentication.getCredentials());
        assertTrue(authentication.isAuthenticated());
    }

    @Test
    void convertThrowsWhenUserFromJwtDoesNotExist() {
        Jwt jwt = jwt("missing-user", Map.of());

        when(customUserDetailsService.loadUserByUsername("missing-user"))
                .thenThrow(new UsernameNotFoundException("User not found: missing-user"));

        assertThrows(UsernameNotFoundException.class, () -> jwtAuthenticationConverter.convert(jwt));
    }

    private Jwt jwt(String subject, Map<String, Object> claims) {
        Instant issuedAt = Instant.now();
        Map<String, Object> mergedClaims = new java.util.HashMap<>(claims);
        mergedClaims.put("sub", subject);

        return new Jwt(
                "token",
                issuedAt,
                issuedAt.plusSeconds(3600),
                Map.of("alg", "none"),
                Map.copyOf(mergedClaims)
        );
    }
}
