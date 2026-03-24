package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.auth.request.ActiveDto;
import com.epam.gymcrm.dto.auth.response.AuthenticationDto;
import com.epam.gymcrm.dto.trainee.request.TraineeUpdateRequestDto;
import com.epam.gymcrm.dto.trainee.request.TraineeTrainerAssignmentRequestDto;
import com.epam.gymcrm.dto.trainee.request.CreateTraineeDto;
import com.epam.gymcrm.dto.trainee.response.TraineeProfileDto;
import com.epam.gymcrm.dto.trainee.response.TraineeTrainingDto;
import com.epam.gymcrm.dto.trainee.response.TrainerDto;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.util.Authentication;
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
    private Authentication authentication;

    @Autowired
    public void setTraineeFacade(TraineeFacade traineeFacade) {
        this.traineeFacade = traineeFacade;
    }

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @Autowired
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @PostMapping("/profile")
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
            @RequestParam("traineeProfile") String traineeProfile,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        authentication.auth(authUsername, authPassword);
        TraineeProfileDto profileDTO = traineeFacade.getTraineeProfile(traineeProfile);

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }


    @PutMapping("/profile")
    ResponseEntity<TraineeProfileDto> updateTraineeProfile(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestBody TraineeUpdateRequestDto traineeDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        authentication.auth(authUsername, authPassword);
        TraineeProfileDto profileDTO = traineeFacade.updateTraineeProfile(
                traineeDto.getUsername(),
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
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestParam("traineeUsername") String traineeUsername,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        authentication.auth(authUsername, authPassword);
        traineeFacade.deleteTrainee(traineeUsername);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/profile/unassigned")
    ResponseEntity<List<TrainerDto>> getActiveTrainersNotAssignedToTrainee(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestParam("traineeUsername") String traineeUsername,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        authentication.auth(authUsername, authPassword);
        List<TrainerDto> trainerDtoList = traineeFacade.getUnassignedTrainersForTrainee(traineeUsername);
        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }

    @PutMapping("/profile/trainers")
    ResponseEntity<List<TrainerDto>> updateTraineeTrainers(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestBody TraineeTrainerAssignmentRequestDto trainerList,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        authentication.auth(authUsername, authPassword);
        List<TrainerDto> trainerDtoList = traineeFacade.updateTraineeTrainers(
                trainerList.getUsername(),
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
        authentication.auth(authUsername, authPassword);
        List<TraineeTrainingDto> trainingDtoList = trainingFacade.getTraineeTrainings(
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
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestBody ActiveDto activeDto
    ) {
        authentication.auth(authUsername, authPassword);
        if (activeDto.isActive()) {
            traineeFacade.activateTrainee(activeDto.getUsername());
        } else {
            traineeFacade.deactivateTrainee(activeDto.getUsername());

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
