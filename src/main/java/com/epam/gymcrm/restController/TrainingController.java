package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.facade.TrainingFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/training")
public class TrainingController {

    private TrainingFacade trainingFacade;

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @PostMapping("/create")
    ResponseEntity<Void> addTraining(
            @RequestHeader("traineeUsername") String traineeUsername,
            @RequestHeader("password") String password,
            @RequestHeader("trainerUsername") String trainerUsername,
            @RequestHeader("trainingName") String trainingName,
            @RequestHeader("trainingDate") LocalDate trainingDate,
            @RequestHeader("trainingDuration") Integer trainingDuration
    ) {
        trainingFacade.addTraining(trainerUsername, password, traineeUsername, new Training(trainingName, trainingDate, trainingDuration));
        return ResponseEntity.ok().build();
    }
}
