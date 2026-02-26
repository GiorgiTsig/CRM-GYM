package com.epam.gymcrm.facade;

import com.epam.gymcrm.dao.searchCriteria.TraineeTrainingSearchCriteria;
import com.epam.gymcrm.dao.searchCriteria.TrainerTrainingSearchCriteria;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.service.TrainingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@Validated
public class TrainingFacade {

    private final TrainingService trainingService;

    public TrainingFacade(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    public void addTraining(
            @NotBlank String trainerUsername,
            @NotBlank String traineeUsername,
            @Valid Training training
    ) {
        trainingService.createTraining(trainerUsername, traineeUsername, training);
    }

    public List<Training> getTraineeTrainings(
            @NotBlank String traineeUsername,
            @NotBlank String password,
            TraineeTrainingSearchCriteria criteria
    ) {
        return trainingService.getTraineeTrainings(traineeUsername, password, criteria);
    }

    public List<Training> getTrainerTrainings(
            @NotBlank String trainerUsername,
            @NotBlank String password,
            TrainerTrainingSearchCriteria criteria
    ) {
        return trainingService.getTrainerTrainings(trainerUsername, password, criteria);
    }
}
