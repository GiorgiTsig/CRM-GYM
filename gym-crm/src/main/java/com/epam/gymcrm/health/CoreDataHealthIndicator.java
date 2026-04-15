package com.epam.gymcrm.health;

import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class CoreDataHealthIndicator implements HealthIndicator {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;

    public CoreDataHealthIndicator(TrainerRepository trainerRepository,
                                   TraineeRepository traineeRepository,
                                   TrainingRepository trainingRepository) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    public Health health() {
        try {
            long trainers = trainerRepository.count();
            long trainees = traineeRepository.count();
            long trainings = trainingRepository.count();

            Status status = trainers > 0 && trainees > 0 ? Status.UP : Status.OUT_OF_SERVICE;
            return Health.status(status)
                    .withDetail("description", "Checks core CRM data readiness using trainer/trainee/training counts")
                    .withDetail("endpoint", "/actuator/health/coreData")
                    .withDetail("statusPolicy", "UP when trainers>0 and trainees>0; OUT_OF_SERVICE otherwise")
                    .withDetail("trainers", trainers)
                    .withDetail("trainees", trainees)
                    .withDetail("trainings", trainings)
                    .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}

