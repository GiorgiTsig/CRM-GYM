package com.epam.gymcrm.repository;

import com.epam.gymcrm.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingRepository extends JpaRepository<Training, UUID> {

    @NonNull
    @Override
    <S extends Training> S save(@NonNull S entity);

    @Modifying
    @Query("DELETE FROM Training t WHERE t.trainee.user.username = :traineeUserUsername")
    void deleteTrainingByTraineeUserUsername(String traineeUserUsername);

    List<Training> findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeNameAndTrainerUserUsername(
            String traineeUserUsername,
            LocalDate dateAfter,
            LocalDate dateBefore,
            String trainingType,
            String trainerUserUsername
    );

    List<Training> findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
            String trainerUserUsername,
            LocalDate dateAfter,
            LocalDate dateBefore,
            String traineeUserUsername
    );
}
