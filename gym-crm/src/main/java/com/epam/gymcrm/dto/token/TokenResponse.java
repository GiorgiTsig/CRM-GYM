package com.epam.gymcrm.dto.token;

public record TokenResponse(
        String access_token,
        String token_type,
        long expires_in
) {}
