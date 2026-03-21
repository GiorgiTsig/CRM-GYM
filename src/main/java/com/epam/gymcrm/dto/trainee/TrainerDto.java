package com.epam.gymcrm.dto.trainee;

import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.dto.UserDto;

public class TrainerDto {
    private UserDto user;
    private TrainingTypeDto trainingType;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public TrainingTypeDto getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingTypeDto trainingType) {
        this.trainingType = trainingType;
    }
}
