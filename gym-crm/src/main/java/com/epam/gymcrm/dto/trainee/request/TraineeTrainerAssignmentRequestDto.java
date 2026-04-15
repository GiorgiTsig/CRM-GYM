package com.epam.gymcrm.dto.trainee.request;

import java.util.Set;

public class TraineeTrainerAssignmentRequestDto {
    private String username;
    private Set<String> trainerUsernames;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getTrainerUsernames() {
        return trainerUsernames;
    }

    public void setTrainerUsernames(Set<String> trainerUsernames) {
        this.trainerUsernames = trainerUsernames;
    }
}
