package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.service.TrainingTypeService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingTypesFacade {

    private TrainingTypeService trainingTypeService;

    public TrainingTypesFacade(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    public List<TrainingType> findAll() {
        return trainingTypeService.findAll();
    }
}
