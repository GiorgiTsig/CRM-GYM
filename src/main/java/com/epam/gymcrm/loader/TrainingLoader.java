package com.epam.gymcrm.loader;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.service.TraineeService;
import com.epam.gymcrm.service.TrainerService;
import com.epam.gymcrm.service.TrainingService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.stream.StreamSupport;

@Component
@Order(3)
@Profile("stg")
public class TrainingLoader extends AbstractDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(TrainingLoader.class);

    private TrainingService trainingService;

    public TrainerService trainerService;

    public TraineeService traineeService;

    @Autowired
    public void setTrainingService(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    public void processTraining(JsonNode node) {
        Training training = objectMapper.convertValue(node, Training.class);
        trainingService.createTraining(
                training.getTrainee().getUser().getUsername(),
                training.getTrainer().getUser().getUsername(),
                new Training(
                        training.getName(), training.getDate(),
                        training.getDuration()
                )
        );
    }

    @Override
    public void processData(JsonNode rootNode) {
        try {
            JsonNode trainingNode = rootNode.get("trainings");
            if (trainingNode == null || !trainingNode.isArray()) {
                logger.warn("No 'trainings' array found in JSON file");
                return;
            }

            // Only VALID trainings reach here
            StreamSupport.stream(trainingNode.spliterator(), false)
                    .filter(training -> {
                        String traineeUsername = training.get("trainee").get("user").get("username").asText();
                        String trainerUsername = training.get("trainer").get("user").get("username").asText();

                        return traineeService.getTrainee(traineeUsername).isPresent()
                                && trainerService.getTrainer(trainerUsername).isPresent();
                    })
                    .forEach(this::processTraining);

        } catch (Exception e) {
            logger.error("Error processing training data", e);
        }
    }
}
