package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.auth.ActiveDto;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainee.request.TraineeUpdateRequestDto;
import com.epam.gymcrm.dto.trainee.request.TraineeTrainerAssignmentRequestDto;
import com.epam.gymcrm.dto.trainee.request.CreateTraineeDto;
import com.epam.gymcrm.dto.trainee.response.TraineeProfileDto;
import com.epam.gymcrm.dto.trainee.response.TraineeTrainingDto;
import com.epam.gymcrm.dto.trainee.response.TrainerDto;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/")
    ResponseEntity<AuthenticationDto> create(
            @RequestBody CreateTraineeDto traineeDto
    ) {
        AuthenticationDto traineeCred = traineeFacade.createTraineeProfile(traineeDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(traineeCred);
    }

    @GetMapping("/profile")
    ResponseEntity<TraineeProfileDto> traineeProfile(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestParam("username") String traineeProfile,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        TraineeProfileDto profileDTO = traineeFacade.getTraineeProfile(
                authUsername,
                authPassword,
                traineeProfile
        );

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }


    @PutMapping("/profile")
    ResponseEntity<TraineeProfileDto> updateTraineeProfile(
            @RequestBody TraineeUpdateRequestDto traineeDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        TraineeProfileDto profileDTO = traineeFacade.updateTraineeProfile(
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

    @DeleteMapping("/profile")
    ResponseEntity<Void> deleteTraineeProfile(
            @RequestBody AuthenticationDto authRequest,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        traineeFacade.deleteTrainee(authRequest.getUsername(), authRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/profile/unassigned")
    ResponseEntity<List<TrainerDto>> getActiveTrainersNotAssignedToTrainee(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        List<TrainerDto> trainerDtoList = traineeFacade.getUnassignedTrainersForTrainee(authUsername, authPassword);
        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }

    @PutMapping("/profile/trainers")
    ResponseEntity<List<TrainerDto>> updateTraineeTrainers(
            @RequestBody TraineeTrainerAssignmentRequestDto trainerList,
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


    @GetMapping("/profile/trainings")
    ResponseEntity<List<TraineeTrainingDto>> getTraineeTrainingsList(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestParam("traineeUsername") String traineeUsername,
            @RequestParam("fromDate") LocalDate fromDate,
            @RequestParam("toDate") LocalDate toDate,
            @RequestParam("trainerUsername") String trainerUsername,
            @RequestParam("trainingType") String trainingType,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ){
        log.info("TransactionId: {}", transactionId);
        List<TraineeTrainingDto> trainingDtoList = trainingFacade.getTraineeTrainings(
                authUsername,
                authPassword,
                traineeUsername,
                fromDate,
                toDate,
                trainerUsername,
                trainingType
        );

        return ResponseEntity.status(HttpStatus.OK).body(trainingDtoList);
    }

    @PatchMapping("/profile/status")
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
