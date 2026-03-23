package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import com.epam.gymcrm.mapper.TrainingTypeMapper;
import com.epam.gymcrm.service.TrainingTypeService;
import com.epam.gymcrm.util.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingTypesFacade {

    private TrainingTypeService trainingTypeService;
    private TrainingTypeMapper trainingTypeMapper;
    private Authentication authentication;

    public TrainingTypesFacade(TrainingTypeService trainingTypeService, TrainingTypeMapper trainingTypeMapper, Authentication authentication) {
        this.trainingTypeService = trainingTypeService;
        this.trainingTypeMapper = trainingTypeMapper;
        this.authentication = authentication;
    }

    public List<TrainingTypeDetailsDto> findAll(String username, String password) {
        authentication.auth(username, password);
        List<TrainingType> trainingTypes = trainingTypeService.findAll();
        return trainingTypes.stream().map(trainingType -> trainingTypeMapper.toTrainingTypeDto(trainingType)).toList();
    }
}
