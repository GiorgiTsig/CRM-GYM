package com.epam.gymcrm.mappper;

import com.epam.gymcrm.domain.*;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainee.TraineeTrainingDto;
import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.dto.trainee.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TraineeMapper {

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    Trainee toTrainee(CreateTraineeDto traineeDto);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "active", source = "user.active")
    TraineeProfileDto toTraineeDto(Trainee trainee);
    UserDto toUserDto(User user);
    TrainerDto toTrainerDto(Trainer trainer);

    @Mapping(target = "trainerUsername", source = "trainer.user.username")
    TraineeTrainingDto toTrainingDto(Training training);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "password", source = "user.password")
    AuthenticationDto toAuth(Trainee trainee);
}
