package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TrainingDaoImp;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.exception.ValidationException;
import com.epam.gymcrm.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class TrainingService {

    private TrainingDaoImp trainingDao;
    private IdGenerator idGenerator;
    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    public void setTrainingDao(TrainingDaoImp trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Training createTraining(Long trainee, Long trainer, String name, TrainingType type, String date, String duration) {
        log.info("Creating training with name: {}, type: {}, date: {}", name, type, date);
        
        if (trainee == null) {
            log.error("Validation failed: Trainee cannot be null");
            throw new ValidationException("Trainee cannot be null");
        }
        if (trainer == null) {
            log.error("Validation failed: Trainer cannot be null");
            throw new ValidationException("Trainer cannot be null");
        }
        validateInput(name, "Training name");
        if (type == null) {
            log.error("Validation failed: Training type cannot be null");
            throw new ValidationException("Training type cannot be null");
        }

        Long id = idGenerator.generateNextId(trainingDao.getAll());

        Training training = new Training(id, trainee, trainer, name, type, date, duration);

        trainingDao.save(training);

        log.info("Training created successfully with id: {}", id);
        return training;
    }

    public Optional<Training> selectTraining(long id) {
        log.info("Selecting training with id: {}", id);
        return trainingDao.get(id);
    }

    public Map<Long, Training> selectAllTrainings() {
        log.info("Selecting all trainings");
        return trainingDao.getAll();
    }
    
    private void validateInput(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            log.error("Validation failed: {} cannot be null or empty", fieldName);
            throw new ValidationException(fieldName + " cannot be null or empty");
        }
    }
}
