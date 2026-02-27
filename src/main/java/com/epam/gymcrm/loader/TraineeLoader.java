package com.epam.gymcrm.loader;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.TraineeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class TraineeLoader extends AbstractDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(TraineeLoader.class);

    private TraineeService traineeService;

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Override
    public void processData(JsonNode rootNode) {
        try {
            JsonNode traineesNode = rootNode.get("trainees");
            if (traineesNode == null || !traineesNode.isArray()) {
                logger.warn("No 'trainees' array found in JSON file");
                return;
            }

            List<Trainee> trainees = objectMapper.convertValue(
                    traineesNode,
                    new TypeReference<List<Trainee>>(){}
            );

            trainees.forEach(trainee ->
                    traineeService.createTraineeProfile(
                            new User(trainee.getUser().getFirstName(), trainee.getUser().getLastName(), trainee.getUser().isActive()),
                            new Trainee(null, trainee.getAddress())
                    )
            );

        } catch (Exception e) {
            logger.error("Error processing trainee data", e);
        }
    }

}
