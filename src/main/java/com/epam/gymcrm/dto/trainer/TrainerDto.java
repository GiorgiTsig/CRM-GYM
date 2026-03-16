package com.epam.gymcrm.dto.trainer;

import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.dto.UserDto;

import java.util.List;

public class TrainerDto {
    private UserDto user;
    private TrainingTypeDto trainingType;
    private List<TraineeDto> trainees;

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

    public List<TraineeDto> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<TraineeDto> trainees) {
        this.trainees = trainees;
    }
}
