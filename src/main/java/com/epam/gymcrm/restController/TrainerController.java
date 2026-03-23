package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.dto.auth.ActiveDto;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.TrainerTrainingsDto;
import com.epam.gymcrm.dto.trainer.TrainingDto;
import com.epam.gymcrm.dto.trainer.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.TrainerDto;
import com.epam.gymcrm.dto.trainer.request.TrainerRequestDto;
import com.epam.gymcrm.dto.trainer.request.TrainerTrainingsRequestDto;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/trainer")
public class TrainerController {
    private static final Logger log = LoggerFactory.getLogger(TrainerController.class);
    private TrainerFacade trainerFacade;
    private TrainingFacade trainingFacade;

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @Autowired
    public void setTrainerFacade(TrainerFacade trainerFacade) {
        this.trainerFacade = trainerFacade;
    }

    @PostMapping("/create")
    ResponseEntity<String> create(
            @RequestBody CreateTrainerDto userTrainerDto
    ) {
        Trainer createdTrainer = trainerFacade.createTrainerProfile(userTrainerDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registration successful " + createdTrainer.getUser().getUsername() + " " + createdTrainer.getUser().getPassword());

    }


    @GetMapping("/get")
    ResponseEntity<TrainerDto> getTrainerProfile(
            @RequestBody AuthenticationDto authController,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        TrainerDto trainerDto = trainerFacade.getTrainerProfile(authController.getUsername(), authController.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(trainerDto);
    }

    @PutMapping("/update")
    ResponseEntity<TrainerDto> updateTraineeProfile(
            @RequestBody TrainerRequestDto trainerRequestDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        TrainerDto profileDTO = trainerFacade.updateTrainerProfile(
                trainerRequestDto.getUsername(),
                trainerRequestDto.getPassword(),
                trainerRequestDto.getFirstName(),
                trainerRequestDto.getLastName(),
                trainerRequestDto.isActive(),
                trainerRequestDto.getSpecialization()
        );

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }

    @GetMapping("/get/trainings")
    ResponseEntity<List<TrainingDto>> getTrainerTrainingsList(
            @RequestBody TrainerTrainingsRequestDto trainerTrainingsRequestDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ){
        log.info("TransactionId: {}", transactionId);

        List<TrainingDto> trainingDtoList = trainingFacade.getTrainerTrainings(
                trainerTrainingsRequestDto.getUsername(),
                trainerTrainingsRequestDto.getPassword(),
                trainerTrainingsRequestDto.getFromDate(),
                trainerTrainingsRequestDto.getToDate(),
                trainerTrainingsRequestDto.getTraineeName()
        );

        return ResponseEntity.status(HttpStatus.OK).body(trainingDtoList);
    }

    @PatchMapping("/status")
    ResponseEntity<Void> updateTraineeStatus (
            @RequestBody ActiveDto activeDto
    ) {
        if (activeDto.isActive()) {
            trainerFacade.activateTrainer(activeDto.getUsername(), activeDto.getPassword());
        } else  {
            trainerFacade.deactivateTrainer(activeDto.getUsername(), activeDto.getPassword());

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
