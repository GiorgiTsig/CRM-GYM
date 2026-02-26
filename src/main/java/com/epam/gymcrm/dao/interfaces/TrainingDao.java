package com.epam.gymcrm.dao.interfaces;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.dao.searchCriteria.TraineeTrainingSearchCriteria;
import com.epam.gymcrm.dao.searchCriteria.TrainerTrainingSearchCriteria;

import java.util.List;

public interface TrainingDao {
    void save(Training training);

    List<Training> findTraineeTrainings(String traineeUsername, TraineeTrainingSearchCriteria criteria);

    List<Training> findTrainerTrainings(String trainerUsername, TrainerTrainingSearchCriteria criteria);

    int deleteTrainingsByTraineeUsername(String traineeUsername);
}
