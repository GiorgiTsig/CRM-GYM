package com.epam.gymcrm.mappper;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingType.TrainingTypeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingTypeDto toTrainingTypeDto(TrainingType trainingTypeDto);
}
