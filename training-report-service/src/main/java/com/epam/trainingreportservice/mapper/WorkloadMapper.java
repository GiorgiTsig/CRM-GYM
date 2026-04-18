package com.epam.trainingreportservice.mapper;

import com.epam.trainingreportservice.domain.TrainerMonthlySummary;
import com.epam.trainingreportservice.dto.response.TrainerWorkloadResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkloadMapper {
    TrainerWorkloadResponse toDto(TrainerMonthlySummary trainer);
}
