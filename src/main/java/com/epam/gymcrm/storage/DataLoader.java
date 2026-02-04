package com.epam.gymcrm.storage;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Iterator;

@Component
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private TraineeStorage traineeStorage;
    private TrainersStorage trainersStorage;
    private TrainingStorage trainingStorage;
    private ObjectMapper objectMapper;

    @Value("${storage.initialization.file.path}")
    private String initializationFilePath;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainersStorage(TrainersStorage trainersStorage) {
        this.trainersStorage = trainersStorage;
    }

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadAll() {
        try {
            String filePath = initializationFilePath.replace("classpath:", "");
            ClassPathResource resource = new ClassPathResource(filePath);
            
            if (!resource.exists()) {
                logger.warn("Initialization file not found: {}. Storage will be empty.", filePath);
                return;
            }

            InputStream inputStream = resource.getInputStream();
            JsonNode rootNode = objectMapper.readTree(inputStream);

            loadTrainees(rootNode);
            loadTrainers(rootNode);
            loadTrainings(rootNode);

            logger.info("Storage initialization completed successfully. " +
                    "Loaded {} trainees, {} trainers, {} trainings",
                    traineeStorage.getTrainees().size(),
                    trainersStorage.getTrainers().size(),
                    trainingStorage.getTraining().size());

        } catch (Exception e) {
            logger.error("Error loading initial data from file: {}", initializationFilePath, e);
            throw new RuntimeException("Failed to initialize storage from file", e);
        }
    }

    private void loadTrainees(JsonNode rootNode) {
        JsonNode traineesNode = rootNode.get("trainees");
        if (traineesNode == null || !traineesNode.isArray()) {
            logger.warn("No 'trainees' array found in JSON file");
            return;
        }

        int count = 0;
        Iterator<JsonNode> iterator = traineesNode.elements();
        while (iterator.hasNext()) {
            JsonNode traineeNode = iterator.next();
            try {
                Trainee trainee = parseTrainee(traineeNode);
                traineeStorage.getTrainees().put(trainee.getId(), trainee);
                count++;
            } catch (Exception e) {
                logger.error("Error parsing trainee: {}", traineeNode, e);
            }
        }
        logger.info("Loaded {} trainees into storage", count);
    }

    private void loadTrainers(JsonNode rootNode) {
        JsonNode trainersNode = rootNode.get("trainers");
        if (trainersNode == null || !trainersNode.isArray()) {
            logger.warn("No 'trainers' array found in JSON file");
            return;
        }

        int count = 0;
        Iterator<JsonNode> iterator = trainersNode.elements();
        while (iterator.hasNext()) {
            JsonNode trainerNode = iterator.next();
            try {
                Trainer trainer = parseTrainer(trainerNode);
                trainersStorage.getTrainers().put(trainer.getId(), trainer);
                count++;
            } catch (Exception e) {
                logger.error("Error parsing trainer: {}", trainerNode, e);
            }
        }
        logger.info("Loaded {} trainers into storage", count);
    }

    private void loadTrainings(JsonNode rootNode) {
        JsonNode trainingsNode = rootNode.get("trainings");
        if (trainingsNode == null || !trainingsNode.isArray()) {
            logger.warn("No 'trainings' array found in JSON file");
            return;
        }

        int count = 0;
        Iterator<JsonNode> iterator = trainingsNode.elements();
        while (iterator.hasNext()) {
            JsonNode trainingNode = iterator.next();
            try {
                Training training = parseTraining(trainingNode);
                if (training != null) {
                    long trainingId = trainingNode.get("id").asLong();
                    trainingStorage.getTraining().put(trainingId, training);
                    count++;
                }
            } catch (Exception e) {
                logger.error("Error parsing training: {}", trainingNode, e);
            }
        }
        logger.info("Loaded {} trainings into storage", count);
    }

    private Trainee parseTrainee(JsonNode node) {
        User user = new User(
                node.get("id").asLong(),
                node.get("firstName").asText(),
                node.get("lastName").asText(),
                node.get("username").asText(),
                node.get("password").asText(),
                node.get("isActive").asBoolean()
        );
        
        return new Trainee(
                node.get("dateOfBirth").asText(),
                node.get("address").asText(),
                user
        );
    }

    private Trainer parseTrainer(JsonNode node) {
        User user = new User(
                node.get("id").asLong(),
                node.get("firstName").asText(),
                node.get("lastName").asText(),
                node.get("username").asText(),
                node.get("password").asText(),
                node.get("isActive").asBoolean()
        );
        
        return new Trainer(user, node.get("specialization").asText());
    }

    /**
     * Parses a Training from JSON node.
     * Looks up Trainee and Trainer from storage by their IDs.
     */
    private Training parseTraining(JsonNode node) {
        long trainingId = node.get("id").asLong();
        long traineeId = node.get("traineeId").asLong();
        long trainerId = node.get("trainerId").asLong();
        
        Trainee trainee = traineeStorage.getTrainees().get(traineeId);
        Trainer trainer = trainersStorage.getTrainers().get(trainerId);
        
        if (trainee == null) {
            logger.error("Trainee with ID {} not found in storage", traineeId);
            return null;
        }
        
        if (trainer == null) {
            logger.error("Trainer with ID {} not found in storage", trainerId);
            return null;
        }
        
        String name = node.get("name").asText();
        String typeStr = node.get("type").asText();
        TrainingType type = TrainingType.valueOf(typeStr);
        String date = node.get("date").asText();
        String duration = node.get("duration").asText();
        
        return new Training(trainingId, trainee, trainer, name, type, date, duration);
    }
}
