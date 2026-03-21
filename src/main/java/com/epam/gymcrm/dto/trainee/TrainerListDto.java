package com.epam.gymcrm.dto.trainee;

import java.util.Set;

public class TrainerListDto {
    private Set<String> trainerUsernames;

    public Set<String> getTrainerUsernames() {
        return trainerUsernames;
    }

    public void setTrainerUsernames(Set<String> trainerUsernames) {
        this.trainerUsernames = trainerUsernames;
    }
}
