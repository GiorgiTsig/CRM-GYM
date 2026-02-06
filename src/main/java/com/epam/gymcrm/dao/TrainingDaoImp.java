package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.storage.TrainingStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDaoImp implements Dao<Training> {
    private static final Logger log = LoggerFactory.getLogger(TrainingDaoImp.class);

    @Autowired
    private TrainingStorage trainingStorage;

    @Override
    public Optional<Training> get(long id) {
        Optional<Training> training = Optional.ofNullable(trainingStorage.getTraining().get(id));
        if (training.isPresent()) {
            log.info("training found with id {}", id);
        } else {
            log.warn("training NOT found with id {}", id);
        }

        return training;
    }

    @Override
    public Map<Long, Training> getAll() {
        return Map.copyOf(trainingStorage.getTraining());
    }

    @Override
    public void save(Training training) {
        trainingStorage.getTraining().put(training.getId(), training);
        log.debug("Current training storage size: {}", trainingStorage.getTraining().size());
    }

    @Override
    public void update(Training training) {
        Map<Long, Training> trainings = trainingStorage.getTraining();

        if (trainings.containsKey(training.getId())) {
            log.info("Updating training with id {}", training.getId());
            trainings.put(training.getId(), training);
        } else {
            log.error("Cannot update. Training with id {} not found", training.getId());
            throw new EntityNotFoundException("Training with id " + training.getId() + " not found");
        }
    }

    @Override
    public void delete(long id) {
        trainingStorage.getTraining().remove(id);
        log.debug("Current training storage size: {}", trainingStorage.getTraining().size());
    }
}
