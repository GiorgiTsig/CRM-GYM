package com.epam.gymcrm.repository;

import com.epam.gymcrm.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TrainingRepository extends JpaRepository<Training, UUID> {

    @NonNull
    @Override
    <S extends Training> S save(@NonNull S entity);

    void deleteTrainingByTraineeId_User_Username(String traineeIdUserUsername);

    List<Training> findTrainingByTraineeId_User_UsernameOrDateBetweenAndTrainerId_TrainingType_TrainingTypeName(
            String traineeIdUserUsername,
            LocalDate dateAfter,
            LocalDate dateBefore,
            String trainerIdTrainingTypeTrainingTypeName
    );

    List<Training> findTrainingByTrainerId_User_UsernameOrDateBetweenAndTraineeId_User_FirstName(
            String trainerIdUserUsername,
            LocalDate dateAfter,
            LocalDate dateBefore,
            String traineeIdUserFirstName
    );
}
