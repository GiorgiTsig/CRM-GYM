package com.epam.gymcrm.loader;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.storage.TraineeStorage;
import com.epam.gymcrm.storage.TrainersStorage;
import com.epam.gymcrm.storage.TrainingStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TrainingLoader extends AbstractDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(TrainingLoader.class);

    private TrainingStorage trainingStorage;

    @Autowired
    public TrainersStorage trainersStorage;

    @Autowired
    public TraineeStorage traineeStorage;

    @Autowired
    private TraineeLoader traineeLoader;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public void processData(JsonNode rootNode) {
        try {
            JsonNode trainingNode = rootNode.get("trainings");
            if (trainingNode == null || !trainingNode.isArray()) {
                logger.warn("No 'trainings' array found in JSON file");
                return;
            }

            List<Integer> indicesToRemove = new ArrayList<>();
            AtomicInteger index = new AtomicInteger(0);

            trainingNode.forEach(training -> {
                int i = index.getAndIncrement();
                ObjectNode trainingObject = (ObjectNode) training;

                Optional<Trainee> trainee = Optional.ofNullable(training.get("traineeId"))
                        .map(JsonNode::asLong)
                        .map(id -> traineeStorage.getTrainees().get(id));

                Optional<Trainer> trainer = Optional.ofNullable(training.get("trainerId"))
                        .map(JsonNode::asLong)
                        .map(id -> trainersStorage.getTrainers().get(id));

                if (trainer.isEmpty() || trainee.isEmpty()) {
                    indicesToRemove.add(i);
                    logger.warn("Skip training {}: missing trainee/trainer", training.get("id"));
                    return;
                }

                JsonNode traineeNode = objectMapper.valueToTree(trainee.get());
                JsonNode trainerNode = objectMapper.valueToTree(trainer.get());

                trainingObject.set("traineeId", traineeNode);
                trainingObject.set("trainerId", trainerNode);
            });

            for (int i = indicesToRemove.size() - 1; i >= 0; i--) {
                ((ArrayNode) trainingNode).remove(indicesToRemove.get(i));
            }


            List<Training> trainings = objectMapper.convertValue(
                    trainingNode,
                    new TypeReference<List<Training>>(){}
            );

            trainings.forEach(training ->
                    trainingStorage.getTraining().put(training.getId(), training)
            );

        } catch (Exception e) {
            logger.error("Error processing training data", e);
        }
    }
}
