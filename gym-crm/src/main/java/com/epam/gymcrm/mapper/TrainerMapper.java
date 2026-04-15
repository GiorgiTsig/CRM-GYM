package com.epam.gymcrm.mapper;

import com.epam.gymcrm.domain.*;
import com.epam.gymcrm.dto.auth.response.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.response.TrainerTrainingDto;
import com.epam.gymcrm.dto.trainer.request.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.response.TraineeDto;
import com.epam.gymcrm.dto.trainer.response.TrainerProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "trainingType.trainingTypeName", source = "trainingTypeName")
    Trainer toTrainer(CreateTrainerDto createTrainerDto);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "trainingType.trainingTypeName")
    @Mapping(target = "active", source = "user.active")
    TrainerProfileDto toTrainerDto(Trainer trainer);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    TraineeDto toTraineeDto(Trainee trainee);

    @Mapping(target = "traineeUsername", source = "trainee.user.username")
    TrainerTrainingDto toTrainingDto(Training training);
}
