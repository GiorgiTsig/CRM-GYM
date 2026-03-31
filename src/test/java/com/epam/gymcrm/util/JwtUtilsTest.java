package com.epam.gymcrm.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private static final String VALID_SECRET_BASE64 = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", VALID_SECRET_BASE64);
    }

    @Test
    void generateTokenIncludesExpectedSubjectAndLifetimeClaims() {
        String token = jwtUtils.generateToken("john");

        Claims claims = Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("john", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void generateTokenThrowsForInvalidBase64Secret() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "%%%not-base64%%%");


        assertThrows(DecodingException.class, () -> jwtUtils.generateToken("john"));
    }

    @Test
    void onAuthenticationSuccessWritesJsonTokenBody() throws Exception {
        when(authentication.getName()).thenReturn("john");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtUtils.onAuthenticationSuccess(request, response, authentication);

        assertEquals("application/json", response.getContentType());
        String responseBody = response.getContentAsString();
        assertTrue(responseBody.startsWith("{\"token\": \""));
        assertTrue(responseBody.endsWith("\"}"));

        String token = responseBody.substring("{\"token\": \"".length(), responseBody.length() - 2);
        Claims claims = Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("john", claims.getSubject());
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(VALID_SECRET_BASE64));
    }
}

