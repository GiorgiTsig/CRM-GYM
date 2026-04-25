package com.epam.trainingreportservice.mapper;

import com.epam.trainingreportservice.domain.Trainer;
import com.epam.trainingreportservice.dto.response.TrainerWorkloadResponse;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper {
    @Mapping(target = "status", source = "status")
    TrainerWorkloadResponse toDto(Trainer trainer);
}