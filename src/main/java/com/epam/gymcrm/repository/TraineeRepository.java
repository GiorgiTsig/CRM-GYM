package com.epam.gymcrm.repository;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, UUID> {

    @EntityGraph(attributePaths = {"trainers", "trainers.user", "user"})
    Optional<Trainee> getTraineeByUserUsername(String userUsername);

    @NonNull
    @Override
    <S extends Trainee> S save(@NonNull S entity);

    void deleteTraineeById(UUID id);

    @NonNull
    @Override
    List<Trainee> findAll();
}
