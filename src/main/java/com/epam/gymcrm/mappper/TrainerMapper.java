package com.epam.gymcrm.mappper;

import com.epam.gymcrm.domain.*;
import com.epam.gymcrm.dto.CreateUserDto;
import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.dto.trainer.TrainingDto;
import com.epam.gymcrm.dto.trainer.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.TraineeDto;
import com.epam.gymcrm.dto.trainer.TrainerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    Trainer toTrainer(CreateTrainerDto createTrainerDto);
    User toUser(CreateUserDto createUserDto);

    TrainerDto toTrainerDto(Trainer trainer);
    UserDto toUserDto(User user);
    TraineeDto toTraineeDto(Trainee trainee);
    TrainingTypeDto toTrainingTypeDto(TrainingTypeMapper trainingType);

    @Mapping(target = "traineeUsername", source = "trainee.user.username")
    TrainingDto toTrainingDto(Training training);
}
