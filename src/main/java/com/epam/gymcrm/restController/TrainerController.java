package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.dto.trainer.TrainerTrainingsDto;
import com.epam.gymcrm.dto.trainer.TrainingDto;
import com.epam.gymcrm.dto.trainer.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.TrainerDto;
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
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        TrainerDto trainerDto = trainerFacade.getTrainerProfile(username, password);
        return ResponseEntity.status(HttpStatus.OK).body(trainerDto);
    }

    @PutMapping("/update")
    ResponseEntity<TrainerDto> updateTraineeProfile(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestBody TrainerDto trainerDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        TrainerDto profileDTO = trainerFacade.updateTrainerProfile(
                username,
                password,
                trainerDto.getFirstName(),
                trainerDto.getLastName(),
                trainerDto.isActive(),
                trainerDto.getSpecialization()
        );

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }

    @GetMapping("/get/trainings")
    ResponseEntity<List<TrainingDto>> getTrainerTrainingsList(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestBody(required = false) TrainerTrainingsDto trainerTrainingsDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ){
        log.info("TransactionId: {}", transactionId);

        List<TrainingDto> trainingDtoList = trainingFacade.getTrainerTrainings(
                username,
                password,
                trainerTrainingsDto.getFromDate(),
                trainerTrainingsDto.getToDate(),
                trainerTrainingsDto.getTraineeName()
        );

        return ResponseEntity.status(HttpStatus.OK).body(trainingDtoList);
    }

    @PatchMapping("/status")
    ResponseEntity<Void> updateTraineeStatus (
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestParam boolean isActive
    ) {
        if (isActive) {
            trainerFacade.activateTrainer(username, password);
        }

        if (!isActive) {
            trainerFacade.deactivateTrainer(username, password);

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
