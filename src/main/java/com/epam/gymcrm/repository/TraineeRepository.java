package com.epam.gymcrm.repository;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
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

    Optional<Trainee> getTraineeByUserUsername(String userUsername);

    @NonNull
    @Override
    <S extends Trainee> S save(@NonNull S entity);

    void deleteTraineeById(UUID id);

    @NonNull
    @Override
    List<Trainee> findAll();

    @Query("""
            SELECT tr
            FROM Trainer tr
            WHERE tr NOT IN (
                SELECT t2
                FROM Trainee ta
                JOIN ta.trainers t2
                WHERE ta.user.username = :username
            )
    """)
    List<Trainer> findUnassignedTrainersByTraineeUsername(@Param("username") String username);
}
