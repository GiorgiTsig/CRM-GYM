package com.epam.gymcrm.integration.steps;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestContext {
    private ResponseEntity<?> response;
    private String username;
    private String password;

    public ResponseEntity<?> getResponse() {
        return response;
    }

    public void setResponse(ResponseEntity<?> response) {
        this.response = response;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
