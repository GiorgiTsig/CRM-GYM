package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingTypeService {
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }


    public List<TrainingType> findAll() {
        return trainingTypeRepository.findAll();
    }
}
