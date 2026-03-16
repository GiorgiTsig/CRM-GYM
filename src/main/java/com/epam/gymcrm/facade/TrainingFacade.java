package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.service.TrainerService;
import com.epam.gymcrm.service.TrainingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Component
@Validated
public class TrainingFacade {

    private final TrainingService trainingService;
    public TrainerService trainerService;

    public TrainingFacade(TrainingService trainingService, TrainerService trainerService) {
        this.trainingService = trainingService;
        this.trainerService = trainerService;
    }

    public Training addTraining(
            @NotBlank String trainerUsername,
            @NotBlank String password,
            @NotBlank String traineeUsername,
            @Valid Training training
    ) {
        trainerService.authenticateTrainer(traineeUsername, password);
       return trainingService.createTraining(trainerUsername, traineeUsername, training);
    }

    public List<Training> getTraineeTrainings(
            @NotBlank String traineeUsername,
            @NotBlank String password,
            @DateTimeFormat LocalDate fromDate,
            @DateTimeFormat LocalDate toDate,
            @NotBlank String trainingType
    ) {
        return trainingService.getTraineeTrainings(traineeUsername, password, fromDate, toDate, trainingType);
    }

    public List<Training> getTrainerTrainings(
            @NotBlank String trainerUsername,
            @NotBlank String password,
            @DateTimeFormat LocalDate fromDate,
            @DateTimeFormat LocalDate toDate,
            @NotBlank String traineeName
    ) {
        return trainingService.getTrainerTrainings(trainerUsername, password, fromDate, toDate, traineeName);
    }
}
