package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.dto.trainee.*;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        TraineeDto profileDTO = traineeFacade.getTraineeProfile(username, password);

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }


    @PutMapping("/update")
    ResponseEntity<TraineeDto> updateTraineeProfile(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader(value = "transactionId", required = false) String transactionId,
            @RequestBody TraineeDto traineeDto
    ) {
        log.info("TransactionId: {}", transactionId);
        TraineeDto profileDTO = traineeFacade.updateTraineeProfile(
                username,
                password,
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
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        traineeFacade.deleteTrainee(username, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/get/unassigned")
    ResponseEntity<List<TrainerDto>> getActiveTrainersNotAssignedToTrainee(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        List<TrainerDto> trainerDtoList = traineeFacade.getUnassignedTrainersForTrainee(username, password);
        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }

    @PutMapping("/update/trainers")
    ResponseEntity<List<TrainerDto>> updateTraineeTrainers(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestBody Set<@NotNull String> trainerList,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        List<TrainerDto> trainerDtoList = traineeFacade.updateTraineeTrainers(username, password, trainerList);
        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }


    @GetMapping("/get/trainings")
    ResponseEntity<List<TrainingDto>> getTraineeTrainingsList(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestBody(required = false) TraineeTrainingsDto traineeTrainingsDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ){
        log.info("TransactionId: {}", transactionId);
        List<TrainingDto> trainingDtoList = trainingFacade.getTraineeTrainings(
                username,
                password,
                traineeTrainingsDto.getFromDate(),
                traineeTrainingsDto.getToDate(),
                traineeTrainingsDto.getTrainingType()
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
            traineeFacade.activateTrainee(username, password);
        }

        if (!isActive) {
            traineeFacade.deactivateTrainee(username, password);

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
