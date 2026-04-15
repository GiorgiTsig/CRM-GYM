package com.epam.gymcrm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorTest {

    private static final String ALLOWED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Test
    void generatePasswordReturns10Characters() {
        PasswordGenerator generator = new PasswordGenerator();

        String password = generator.generatePassword();

        assertEquals(10, password.length());
    }

    @Test
    void generatePasswordContainsOnlyAllowedCharacters() {
        PasswordGenerator generator = new PasswordGenerator();

        String password = generator.generatePassword();

        assertTrue(password.chars().allMatch(ch -> ALLOWED.indexOf(ch) >= 0),
                "Password should use only alphanumeric characters");
    }
}
