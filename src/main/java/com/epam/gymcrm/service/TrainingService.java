package com.epam.gymcrm.service;

import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.exception.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Service
@Validated
public class TrainingService {

    private TrainingRepository trainingRepository;
    private TrainerService trainerService;
    private TraineeService traineeService;
    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Transactional
    public void createTraining(
            @NotBlank String traineeUsername,
            @NotBlank String trainerUsername,
            @Valid Training training
    ) {
        Trainer trainer = trainerService.getTrainer(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));

        Trainee trainee = traineeService.findTraineeByUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));


        if (trainer.getTrainingType() == null) {
            throw new IllegalArgumentException("Training type is required for training creation");
        }

        trainee.getTrainers().add(trainer);

        TrainingType type = trainerService.trainingType(trainer.getTrainingType().getTrainingTypeName());

        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setType(type);

        trainingRepository.save(training);
    }

    @Transactional
    public void delete(String username) {
        trainingRepository.deleteTrainingByTraineeUserUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(
            @NotBlank String traineeUsername,
            @NotBlank String password,
            @DateTimeFormat LocalDate fromDate,
            @DateTimeFormat LocalDate toDate,
            @NotBlank String trainingType
    ) {
        if (!traineeService.authenticateTrainee(traineeUsername, password)) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
        log.info("Selecting trainee trainings with username: {}", traineeUsername);
        return trainingRepository.findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeName(
                traineeUsername,
                fromDate,
                toDate,
                trainingType
        );
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(
            @NotBlank String trainerUsername,
            @NotBlank String password,
            @DateTimeFormat LocalDate fromDate,
            @DateTimeFormat LocalDate toDate,
            @NotBlank String traineeName
    ) {
        if (!trainerService.authenticateTrainer(trainerUsername, password)) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
        log.info("Selecting trainer trainings with username: {}", trainerUsername);
        return trainingRepository.findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                trainerUsername,
                fromDate,
                toDate,
                traineeName
        );
    }
}
