package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import com.epam.gymcrm.mapper.TrainingTypeMapper;
import com.epam.gymcrm.service.TrainingTypeService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingTypesFacade {

    private TrainingTypeService trainingTypeService;
    private TrainingTypeMapper trainingTypeMapper;

    public TrainingTypesFacade(TrainingTypeService trainingTypeService, TrainingTypeMapper trainingTypeMapper) {
        this.trainingTypeService = trainingTypeService;
        this.trainingTypeMapper = trainingTypeMapper;
    }

    public List<TrainingTypeDetailsDto> findAll() {
        List<TrainingType> trainingTypes = trainingTypeService.findAll();
        return trainingTypes.stream().map(trainingType -> trainingTypeMapper.toTrainingTypeDto(trainingType)).toList();
    }
}
