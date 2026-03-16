package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.dto.trainee.TrainingDto;
import com.epam.gymcrm.dto.trainee.*;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.mappper.TraineeMapper;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/trainee")
public class TraineeController {

    private static final Logger log = LoggerFactory.getLogger(TraineeController.class);
    private TraineeFacade traineeFacade;
    private TraineeMapper traineeMapper;
    private TrainingFacade trainingFacade;

    @Autowired
    public void setTraineeFacade(TraineeFacade traineeFacade) {
        this.traineeFacade = traineeFacade;
    }

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @PostMapping("/create")
    ResponseEntity<String> create(
            @RequestBody CreateTraineeDto traineeDto
    ) {
        try {
            Trainee trainee = traineeMapper.toTrainee(traineeDto);
            Trainee createdTrainee = traineeFacade.createTraineeProfile(trainee.getUser(), trainee);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Registration successful " + createdTrainee.getUser().getUsername() + " " + createdTrainee.getUser().getPassword());

        } catch (ConstraintViolationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/get")
    ResponseEntity<TraineeDto> traineeProfile(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        Trainee trainee = traineeFacade.getTraineeProfile(username, password).orElseThrow();
        TraineeDto profileDTO = traineeMapper.toTraineeDto(trainee);

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }


    @PutMapping("/update")
    ResponseEntity<TraineeDto> updateTraineeProfile(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader("firstName") String firstName,
            @RequestHeader("lastName") String lastName,
            @RequestHeader("dateOfBirth") LocalDate dateOfBirth,
            @RequestHeader("address") String address,
            @RequestHeader("isActive") boolean isActive,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        Trainee trainee = traineeFacade.updateTraineeProfile(username, password, firstName, lastName, dateOfBirth, address, isActive);
        TraineeDto profileDTO = traineeMapper.toTraineeDto(trainee);

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
        List<Trainer> trainers = traineeFacade.getUnassignedTrainersForTrainee(username, password);
        List<TrainerDto> trainerDtoList = trainers.stream().map(trainer -> traineeMapper.toTrainerDto(trainer)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }

    @PutMapping("/update/trainers")
    ResponseEntity<List<TrainerDto>> updateTraineeTrainers(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader("trainees") Set<String> trainerList,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        List<Trainer> trainers = traineeFacade.updateTraineeTrainers(username, password, trainerList);
        List<TrainerDto> trainerDtoList = trainers.stream().map(trainer -> traineeMapper.toTrainerDto(trainer)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }


    @GetMapping("/get/trainings")
    ResponseEntity<List<TrainingDto>> getTraineeTrainingsList(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader("fromDate") LocalDate fromDate,
            @RequestHeader("toDate") LocalDate toDate,
            @RequestHeader("type") String trainingType,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ){
        log.info("TransactionId: {}", transactionId);
        List<Training> trainings = trainingFacade.getTraineeTrainings(username, password, fromDate, toDate, trainingType);
        List<TrainingDto> trainingDtoList = trainings.stream().map(training -> traineeMapper.toTrainingDto(training)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(trainingDtoList);
    }

    @PatchMapping("/status")
    ResponseEntity<Void> updateTraineeStatus (
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader("isActive") boolean isActive
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
