package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.service.TraineeService;
import com.epam.gymcrm.service.TrainerService;
import com.epam.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GymFacade {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public GymFacade(TrainerService trainerService,
                     TraineeService traineeService,
                     TrainingService trainingService
    ) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        return trainerService.createTrainer(firstName, lastName, specialization);
    }

    public Map<Long, Trainer> getAllTrainers() {
        return trainerService.selectAllTrainers();
    }

    public Trainee createTrainee(String firstName, String lastName, String dateOfBirth, String address) {
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }

    public Training createTraining(Long trainee, Long trainer, String name, TrainingType type, String date, String duration) {
        return trainingService.createTraining(trainee, trainer, name, type, date, duration);
    }
}
