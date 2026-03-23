package com.epam.gymcrm.mappper;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingTypeDetailsDto toTrainingTypeDto(TrainingType trainingTypeDto);
}
