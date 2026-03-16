package com.epam.gymcrm.mappper;

import com.epam.gymcrm.domain.*;
import com.epam.gymcrm.dto.CreateUserDto;
import com.epam.gymcrm.dto.trainee.TrainingDto;
import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.dto.trainee.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    Trainee toTrainee(CreateTraineeDto traineeDto);
    User toUser(CreateUserDto createUserDto);

    TraineeDto toTraineeDto(Trainee trainee);
    UserDto toUserDto(User user);
    TrainerDto toTrainerDto(Trainer trainer);
    TrainingTypeDto toTrainingTypeDto(TrainingTypeMapper trainingType);

    @Mapping(target = "trainerUsername", source = "trainer.user.username")
    TrainingDto toTrainingDto(Training training);
}
