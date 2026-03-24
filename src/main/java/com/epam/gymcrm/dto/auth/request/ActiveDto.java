package com.epam.gymcrm.dto.auth.request;

public class ActiveDto {
    private String username;
    private boolean isActive;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
