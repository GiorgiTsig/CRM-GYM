package com.epam.gymcrm.repository;

import com.epam.gymcrm.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, UUID> {

    Optional<Trainer> getTrainerByUser_Username(String userUsername);

    @NonNull
    @Override
    <S extends Trainer> S save(@NonNull S entity);

    void deleteTrainerById(UUID id);
}
