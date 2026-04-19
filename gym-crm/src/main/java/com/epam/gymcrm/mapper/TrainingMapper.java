package com.epam.gymcrm.mapper;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.dto.training.TrainingEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    @Mapping(source = "trainer.user.username", target = "trainerUsername")
    @Mapping(source = "trainer.user.firstName", target = "firstName")
    @Mapping(source = "trainer.user.lastName", target = "lastName")
    @Mapping(source = "trainer.user.active", target = "active")
    @Mapping(source = "date", target = "trainingDate")
    @Mapping(source = "duration", target = "duration")
    TrainingEventDto toEventDto(Training training);
}
