package com.epam.gymcrm.loader;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.storage.TrainersStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainersLoader extends AbstractDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(TrainersLoader.class);

    private TrainersStorage trainersStorage;

    @Autowired
    public void setTrainersStorage(TrainersStorage trainersStorage) {
        this.trainersStorage = trainersStorage;
    }

    @Override
    public void processData(JsonNode rootNode) {
        try {
            JsonNode trainersNode = rootNode.get("trainers");
            if (trainersNode == null || !trainersNode.isArray()) {
                logger.warn("No 'trainers' array found in JSON file");
                return;
            }

            List<Trainer> trainers = objectMapper.convertValue(
                    trainersNode,
                    new TypeReference<List<Trainer>>(){}
            );

            trainers.forEach(trainer ->
                    trainersStorage.getTrainers().put(trainer.getId(), trainer)
            );

        } catch (Exception e) {
            logger.error("Error processing trainee data", e);
        }
    }
}
