package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.dto.auth.ActiveDto;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainee.*;
import com.epam.gymcrm.dto.trainee.request.TraineeTrainingsRequestDto;
import com.epam.gymcrm.dto.trainee.request.TraineeUpdateRequestDto;
import com.epam.gymcrm.dto.trainee.request.TrainerRequestDto;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/trainee")
public class TraineeController {

    private static final Logger log = LoggerFactory.getLogger(TraineeController.class);
    private TraineeFacade traineeFacade;
    private TrainingFacade trainingFacade;

    @Autowired
    public void setTraineeFacade(TraineeFacade traineeFacade) {
        this.traineeFacade = traineeFacade;
    }

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @PostMapping("/create")
    ResponseEntity<String> create(
            @RequestBody CreateTraineeDto traineeDto
    ) {
        Trainee trainee = traineeFacade.createTraineeProfile(traineeDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registration successful " + trainee.getUser().getUsername() + " " + trainee.getUser().getPassword());
    }

    @GetMapping("/get")
    ResponseEntity<TraineeDto> traineeProfile(
            @RequestBody AuthenticationDto authController,
            @RequestParam("trainee") String traineeProfile,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        TraineeDto profileDTO = traineeFacade.getTraineeProfile(
                authController.getUsername(),
                authController.getPassword(),
                traineeProfile
        );

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }


    @PutMapping("/update")
    ResponseEntity<TraineeDto> updateTraineeProfile(
            @RequestBody TraineeUpdateRequestDto traineeDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        TraineeDto profileDTO = traineeFacade.updateTraineeProfile(
                traineeDto.getUsername(),
                traineeDto.getPassword(),
                traineeDto.getFirstName(),
                traineeDto.getLastName(),
                traineeDto.getDateOfBirth(),
                traineeDto.getAddress(),
                traineeDto.isActive()
        );

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteTraineeProfile(
            @RequestBody AuthenticationDto authController,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        traineeFacade.deleteTrainee(authController.getUsername(), authController.getPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/get/unassigned")
    ResponseEntity<List<TrainerDto>> getActiveTrainersNotAssignedToTrainee(
            @RequestBody AuthenticationDto authController,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        List<TrainerDto> trainerDtoList = traineeFacade.getUnassignedTrainersForTrainee(authController.getUsername(), authController.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }

    @PutMapping("/update/trainers")
    ResponseEntity<List<TrainerDto>> updateTraineeTrainers(
            @RequestBody TrainerRequestDto trainerList,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        List<TrainerDto> trainerDtoList = traineeFacade.updateTraineeTrainers(
                trainerList.getUsername(),
                trainerList.getPassword(),
                trainerList.getTrainerUsernames()
        );

        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }


    @GetMapping("/get/trainings")
    ResponseEntity<List<TrainingDto>> getTraineeTrainingsList(
            @RequestBody TraineeTrainingsRequestDto traineeTrainingsRequestDtoDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ){
        log.info("TransactionId: {}", transactionId);
        List<TrainingDto> trainingDtoList = trainingFacade.getTraineeTrainings(
                traineeTrainingsRequestDtoDto.getUsername(),
                traineeTrainingsRequestDtoDto.getPassword(),
                traineeTrainingsRequestDtoDto.getFromDate(),
                traineeTrainingsRequestDtoDto.getToDate(),
                traineeTrainingsRequestDtoDto.getTrainingType()
        );

        return ResponseEntity.status(HttpStatus.OK).body(trainingDtoList);
    }

    @PatchMapping("/status")
    ResponseEntity<Void> updateTraineeStatus (
            @RequestBody ActiveDto activeDto
    ) {
        if (activeDto.isActive()) {
            traineeFacade.activateTrainee(activeDto.getUsername(), activeDto.getPassword());
        } else {
            traineeFacade.deactivateTrainee(activeDto.getUsername(), activeDto.getPassword());

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
