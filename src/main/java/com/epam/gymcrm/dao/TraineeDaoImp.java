package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.storage.TraineeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDaoImp implements Dao<Trainee> {

    @Autowired
    private TraineeStorage traineeStorage;

    private static final Logger log = LoggerFactory.getLogger(TraineeDaoImp.class);

    @Override
    public Optional<Trainee> get(long id) {
        Optional<Trainee> trainee = Optional.ofNullable(traineeStorage.getTrainees().get(id));
        if (trainee.isPresent()) {
            log.info("Trainee found with id {}", id);
        } else {
            log.warn("Trainee NOT found with id {}", id);
        }

        return trainee;
    }

    @Override
    public Map<Long, Trainee> getAll() {
        return Map.copyOf(traineeStorage.getTrainees());
    }

    @Override
    public void save(Trainee trainee) {
        traineeStorage.getTrainees().put(trainee.getId(), trainee);
        log.debug("Current trainee storage size: {}", traineeStorage.getTrainees().size());
    }

    @Override
    public void update(Trainee trainee) {
        Map<Long, Trainee> trainees = traineeStorage.getTrainees();

        if (trainees.containsKey(trainee.getId())) {
            log.info("Updating trainee with id {}", trainee.getId());
            trainees.put(trainee.getId(), trainee);
        } else {
            log.error("Cannot update. Trainee with id {} not found", trainee.getId());
            throw new EntityNotFoundException("Trainee with id " + trainee.getId() + " not found");
        }
    }

    @Override
    public void delete(long id) {
        traineeStorage.getTrainees().remove(id);
        log.debug("Current trainee storage size: {}", traineeStorage.getTrainees().size());
    }
}