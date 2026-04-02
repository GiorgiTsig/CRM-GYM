package com.epam.gymcrm.util;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtDecoderService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
