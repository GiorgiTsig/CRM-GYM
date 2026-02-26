package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TrainingDaoImp;
import com.epam.gymcrm.dao.searchCriteria.TraineeTrainingSearchCriteria;
import com.epam.gymcrm.dao.searchCriteria.TrainerTrainingSearchCriteria;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.exception.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class TrainingService {

    private TrainingDaoImp trainingDao;
    private TrainerService trainerService;
    private TraineeService traineeService;
    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    public void setTrainingDao(TrainingDaoImp trainingDao) {
        this.trainingDao = trainingDao;
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
            @NotBlank String trainerUsername,
            @NotBlank String traineeUsername,
            @Valid Training training
    ) {
        Trainer trainer = trainerService.findTrainerByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));

        Trainee trainee = traineeService.findTraineeByUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));

        if (trainer.getTrainingType() == null) {
            throw new IllegalArgumentException("Training type is required for training creation");
        }
        TrainingType type = trainerService.trainingType(trainer.getTrainingType().getTrainingTypeName());

        training.setTrainerId(trainer);
        training.setTraineeId(trainee);
        training.setType(type);

        trainingDao.save(training);
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(
            @NotBlank String traineeUsername,
            @NotBlank String password,
            TraineeTrainingSearchCriteria criteria
    ) {
        if (!traineeService.authenticateTrainee(traineeUsername, password)) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
        log.info("Selecting trainee trainings with username: {}", traineeUsername);
        return trainingDao.findTraineeTrainings(traineeUsername, criteria);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(
            @NotBlank String trainerUsername,
            @NotBlank String password,
            TrainerTrainingSearchCriteria criteria
    ) {
        if (!trainerService.authenticateTrainer(trainerUsername, password)) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
        log.info("Selecting trainer trainings with username: {}", trainerUsername);
        return trainingDao.findTrainerTrainings(trainerUsername, criteria);
    }
}
