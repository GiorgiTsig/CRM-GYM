package com.epam.gymcrm.service;

import com.epam.gymcrm.client.AuthClient;
import com.epam.gymcrm.dto.token.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

@Service
public class TokenService {

    private static final long EXPIRY_SKEW_SECONDS = 10;

    private final AuthClient authClient;
    private final Object lock = new Object();

    private volatile String cachedToken;
    private volatile Instant expiresAt;

    @Value("${auth.client.id}")
    private String clientId;

    @Value("${auth.client.secret}")
    private String clientSecret;

    public TokenService(AuthClient authClient) {
        this.authClient = authClient;
    }

    public String getToken() {
        Instant now = Instant.now();
        if (cachedToken != null && expiresAt != null && now.isBefore(expiresAt)) {
            return cachedToken;
        }

        synchronized (lock) {
            now = Instant.now();
            if (cachedToken != null && expiresAt != null && now.isBefore(expiresAt)) {
                return cachedToken;
            }

            String credentials = clientId + ":" + clientSecret;
            String basicAuth = "Basic " + Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            TokenResponse response = authClient.getToken(
                    basicAuth,
                    "grant_type=client_credentials"
            );

            cachedToken = Objects.requireNonNull(response.access_token(), "Auth server returned no access token");
            long ttl = Math.max(1, response.expires_in() - EXPIRY_SKEW_SECONDS);
            expiresAt = now.plusSeconds(ttl);
            return cachedToken;
        }
    }
}
