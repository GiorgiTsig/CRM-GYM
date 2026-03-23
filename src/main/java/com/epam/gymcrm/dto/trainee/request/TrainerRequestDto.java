package com.epam.gymcrm.dto.trainee.request;

import java.util.Set;

public class TrainerRequestDto {
    private String username;
    private String password;
    private Set<String> trainerUsernames;

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

    public Set<String> getTrainerUsernames() {
        return trainerUsernames;
    }

    public void setTrainerUsernames(Set<String> trainerUsernames) {
        this.trainerUsernames = trainerUsernames;
    }
}
