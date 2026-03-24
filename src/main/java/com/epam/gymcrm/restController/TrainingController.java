package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.facade.TrainingTypesFacade;
import com.epam.gymcrm.util.Authentication;
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
    private Authentication authentication;

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @Autowired
    public void setTrainingTypesFacade(TrainingTypesFacade trainingTypesFacade) {
        this.trainingTypesFacade = trainingTypesFacade;
    }

    @Autowired
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @PostMapping
    ResponseEntity<Void> addTraining(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestBody TrainingRequestDto trainingRequestDto
    ) {
        authentication.auth(authUsername, authPassword);
        trainingFacade.addTraining(trainingRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/types")
    ResponseEntity<List<TrainingTypeDetailsDto>> getTrainingType(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword
    ) {
        authentication.auth(authUsername, authPassword);
        List<TrainingTypeDetailsDto> trainingTypesDto =  trainingTypesFacade.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(trainingTypesDto);
    }

}
