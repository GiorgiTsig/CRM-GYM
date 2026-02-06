package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.storage.TrainersStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDaoImp implements Dao<Trainer> {

    @Autowired
    private TrainersStorage trainersStorage;

    private static final Logger log = LoggerFactory.getLogger(TrainerDaoImp.class);

    @Override
    public Optional<Trainer> get(long id) {
        Optional<Trainer> trainer = Optional.ofNullable(trainersStorage.getTrainers().get(id));
        if (trainer.isPresent()) {
            log.info("Trainer found with id {}", id);
        } else {
            log.warn("Trainer NOT found with id {}", id);
        }

        return trainer;
    }

    @Override
    public Map<Long, Trainer> getAll() {
        return Map.copyOf(trainersStorage.getTrainers());
    }

    @Override
    public void save(Trainer trainer) {
        trainersStorage.getTrainers().put(trainer.getId(), trainer);
        log.debug("Current trainer storage size: {}", trainersStorage.getTrainers().size());
    }

    @Override
    public void update(Trainer trainer) {
        Map<Long, Trainer> trainers = trainersStorage.getTrainers();

        if (trainers.containsKey(trainer.getId())) {
            log.info("Updating trainer with id {}", trainer.getId());
            trainers.put(trainer.getId(), trainer);
        } else {
            log.error("Cannot update. Trainer with id {} not found", trainer.getId());
            throw new EntityNotFoundException("Trainee with id " + trainer.getId() + " not found");
        }
    }

    @Override
    public void delete(long id) {
        trainersStorage.getTrainers().remove(id);
        log.debug("Current trainer storage size: {}", trainersStorage.getTrainers().size());
    }
}
