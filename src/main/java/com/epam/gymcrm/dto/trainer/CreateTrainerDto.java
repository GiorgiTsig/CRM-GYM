package com.epam.gymcrm.dto.trainer;

import com.epam.gymcrm.dto.CreateUserDto;
import com.epam.gymcrm.dto.TrainingTypeDto;

public class CreateTrainerDto {
    private TrainingTypeDto trainingType;
    private CreateUserDto user;

    public TrainingTypeDto getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingTypeDto trainingType) {
        this.trainingType = trainingType;
    }

    public CreateUserDto getUser() {
        return user;
    }

    public void setUser(CreateUserDto user) {
        this.user = user;
    }
}
