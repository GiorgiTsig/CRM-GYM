package com.epam.trainingreportservice.repository;

import com.epam.trainingreportservice.domain.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends MongoRepository<Trainer, String> {
    Optional<Trainer> findByTrainerUsername(String trainerUsername);
}
