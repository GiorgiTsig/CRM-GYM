package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.auth.ActiveDto;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.response.TrainerTrainingDto;
import com.epam.gymcrm.dto.trainer.request.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.response.TrainerProfileDto;
import com.epam.gymcrm.dto.trainer.request.TrainerProfileUpdateRequestDto;
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

    @PostMapping("/")
    ResponseEntity<AuthenticationDto> create(
            @RequestBody CreateTrainerDto userTrainerDto
    ) {
        AuthenticationDto trainerCred = trainerFacade.createTrainerProfile(userTrainerDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainerCred);

    }


    @GetMapping("/profile")
    ResponseEntity<TrainerProfileDto> getTrainerProfile(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        TrainerProfileDto trainerDto = trainerFacade.getTrainerProfile(authUsername, authPassword);
        return ResponseEntity.status(HttpStatus.OK).body(trainerDto);
    }

    @PutMapping("/profile")
    ResponseEntity<TrainerProfileDto> updateTraineeProfile(
            @RequestBody TrainerProfileUpdateRequestDto trainerRequestDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        TrainerProfileDto profileDTO = trainerFacade.updateTrainerProfile(
                trainerRequestDto.getUsername(),
                trainerRequestDto.getPassword(),
                trainerRequestDto.getFirstName(),
                trainerRequestDto.getLastName(),
                trainerRequestDto.isActive(),
                trainerRequestDto.getSpecialization()
        );

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }

    @GetMapping("/profile/trainings")
    ResponseEntity<List<TrainerTrainingDto>> getTrainerTrainingsList(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestBody TrainerTrainingsRequestDto trainerTrainingsRequestDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ){
        log.info("TransactionId: {}", transactionId);

        List<TrainerTrainingDto> trainingDtoList = trainingFacade.getTrainerTrainings(
                authUsername,
                authPassword,
                trainerTrainingsRequestDto.getFromDate(),
                trainerTrainingsRequestDto.getToDate(),
                trainerTrainingsRequestDto.getTraineeName()
        );

        return ResponseEntity.status(HttpStatus.OK).body(trainingDtoList);
    }

    @PatchMapping("/profile/status")
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
