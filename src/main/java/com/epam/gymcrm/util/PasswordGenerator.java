package com.epam.gymcrm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {

    private static final Logger log = LoggerFactory.getLogger(PasswordGenerator.class);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private final SecureRandom random;

    public PasswordGenerator() {
        this.random = new SecureRandom();
    }

    /**
     * Generates a random password of 10 characters length.
     * Uses alphanumeric characters (A-Z, a-z, 0-9).
     *
     * @return Random password string
     */
    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        log.debug("Generated password");
        return password.toString();
    }
}
