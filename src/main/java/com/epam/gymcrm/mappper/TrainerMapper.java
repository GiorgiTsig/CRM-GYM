package com.epam.gymcrm.mappper;

import com.epam.gymcrm.domain.*;
import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.dto.trainer.TrainerTrainingDto;
import com.epam.gymcrm.dto.trainer.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.TraineeDto;
import com.epam.gymcrm.dto.trainer.TrainerTraineeListItemDto;
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
    TrainerTraineeListItemDto toTrainerDto(Trainer trainer);
    UserDto toUserDto(User user);
    TraineeDto toTraineeDto(Trainee trainee);

    @Mapping(target = "traineeUsername", source = "trainee.user.username")
    TrainerTrainingDto toTrainingDto(Training training);
}
