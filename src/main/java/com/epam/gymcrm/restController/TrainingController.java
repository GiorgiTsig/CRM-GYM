package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingType.TrainingTypeDto;
import com.epam.gymcrm.dto.trainee.TrainingDto;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.facade.TrainingTypesFacade;
import com.epam.gymcrm.mappper.TrainingTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/training")
public class TrainingController {

    private TrainingFacade trainingFacade;
    private TrainingTypesFacade trainingTypesFacade;
    private TrainingTypeMapper trainingTypeMapper;

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @Autowired
    public void setTrainingTypesFacade(TrainingTypesFacade trainingTypesFacade) {
        this.trainingTypesFacade = trainingTypesFacade;
    }

    @Autowired
    public void setTrainingTypeMapper(TrainingTypeMapper trainingTypeMapper) {
        this.trainingTypeMapper = trainingTypeMapper;
    }

    @PostMapping("/create")
    ResponseEntity<Void> addTraining(
            @RequestHeader("traineeUsername") String traineeUsername,
            @RequestHeader("password") String password,
            @RequestBody TrainingDto trainingDto
    ) {
        trainingFacade.addTraining(traineeUsername, password, trainingDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get")
    ResponseEntity<List<TrainingTypeDto>> getTrainingType() {
        List<TrainingType> trainingTypes =  trainingTypesFacade.findAll();
        var trainingTypesDto = trainingTypes.stream().map(trainingType -> trainingTypeMapper.toTrainingTypeDto(trainingType)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(trainingTypesDto);
    }

}
