package com.epam.gymcrm.loader;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.storage.TraineeStorage;
import com.epam.gymcrm.storage.TrainersStorage;
import com.epam.gymcrm.storage.TrainingStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Component
public class TrainingLoader extends AbstractDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(TrainingLoader.class);

    private TrainingStorage trainingStorage;

    public TrainersStorage trainersStorage;

    public TraineeStorage traineeStorage;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Autowired
    public void setTrainersStorage(TrainersStorage trainersStorage) {
        this.trainersStorage = trainersStorage;
    }

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    public void processTraining(JsonNode node) {
        Training trainingMapper = objectMapper.convertValue(node, Training.class);
        trainingStorage.getTraining().put(trainingMapper.getId(), trainingMapper);
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
                        long traineeId = training.get("traineeId").asLong();
                        long trainerId = training.get("trainerId").asLong();

                        return traineeStorage.getTrainees().containsKey(traineeId)
                                && trainersStorage.getTrainers().containsKey(trainerId);
                    })
                    .forEach(this::processTraining);

        } catch (Exception e) {
            logger.error("Error processing training data", e);
        }
    }
}
