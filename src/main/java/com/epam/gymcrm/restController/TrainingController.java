package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.facade.TrainingTypesFacade;
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

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @Autowired
    public void setTrainingTypesFacade(TrainingTypesFacade trainingTypesFacade) {
        this.trainingTypesFacade = trainingTypesFacade;
    }

    @PostMapping("/create")
    ResponseEntity<Void> addTraining(
            @RequestBody TrainingRequestDto trainingRequestDto
    ) {
        trainingFacade.addTraining(trainingRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/training")
    ResponseEntity<List<TrainingTypeDetailsDto>> getTrainingType() {
        List<TrainingTypeDetailsDto> trainingTypesDto =  trainingTypesFacade.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(trainingTypesDto);
    }

}
