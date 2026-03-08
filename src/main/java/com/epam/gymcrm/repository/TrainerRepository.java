package com.epam.gymcrm.repository;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, UUID> {

    Optional<Trainer> getTrainerByUserUsername(String userUsername);

    @NonNull
    @Override
    <S extends Trainer> S save(@NonNull S entity);

    void deleteTrainerById(UUID id);

    @NonNull
    @Override
    List<Trainer> findAll();

    Set<Trainer> findAllByUserUsernameIn(Collection<String> username);
}
