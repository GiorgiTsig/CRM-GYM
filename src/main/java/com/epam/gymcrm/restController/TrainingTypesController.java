package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingType.TrainingTypeDto;
import com.epam.gymcrm.mappper.TrainerMapper;
import com.epam.gymcrm.mappper.TrainingTypeMapper;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training")
public class TrainingTypesController {

    private TrainingTypeRepository trainingTypeRepository;

    private TrainingTypeMapper trainingTypeMapper;

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Autowired
    public void setTrainerMapper(TrainingTypeMapper trainingTypeMapper) {
        this.trainingTypeMapper = trainingTypeMapper;
    }

    @GetMapping("/get")
    ResponseEntity<List<TrainingTypeDto>> getTrainingType() {
        List<TrainingType> trainingTypes =  trainingTypeRepository.findAll();
        var trainingTypesDto = trainingTypes.stream().map(trainingType -> trainingTypeMapper.toTrainingTypeDto(trainingType)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(trainingTypesDto);
    }
}
