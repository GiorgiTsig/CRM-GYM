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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Trainee", description = "Operations for trainee profile, assignments, and trainee trainings")
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

    @PostMapping("/profile")
    @Operation(summary = "Create trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee profile created"),
            @ApiResponse(responseCode = "400", description = "Invalid trainee data"),
            @ApiResponse(responseCode = "409", description = "Trainee profile already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<AuthenticationDto> create(
            @RequestBody CreateTraineeDto traineeDto
    ) {
        AuthenticationDto traineeCred = traineeFacade.createTraineeProfile(traineeDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(traineeCred);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile returned"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TraineeProfileDto> traineeProfile(
            @RequestParam("traineeProfile") String traineeProfile,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        log.info("X-Correlation-Id: {}", correlationId);

        TraineeProfileDto profileDTO = traineeFacade.getTraineeProfile(traineeProfile);

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }


    @PutMapping("/profile")
    @Operation(summary = "Update trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile updated"),
            @ApiResponse(responseCode = "400", description = "Invalid update request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "409", description = "Update conflicts with current trainee state"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TraineeProfileDto> updateTraineeProfile(
            @RequestBody TraineeUpdateRequestDto traineeDto,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        log.info("X-Correlation-Id: {}", correlationId);
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
    @Operation(summary = "Delete trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> deleteTraineeProfile(
            @RequestParam("traineeUsername") String traineeUsername,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        log.info("X-Correlation-Id: {}", correlationId);
        traineeFacade.deleteTrainee(traineeUsername);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("{username}/unassigned-traineers")
    @Operation(summary = "Get active trainers not assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unassigned active trainers returned"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TrainerDto>> getActiveTrainersNotAssignedToTrainee(
            @PathVariable("username") String traineeUsername,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        log.info("X-Correlation-Id: {}", correlationId);
        List<TrainerDto> trainerDtoList = traineeFacade.getUnassignedTrainersForTrainee(traineeUsername);
        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }

    @PutMapping("/profile/trainers")
    @Operation(summary = "Update trainee trainer assignments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee trainers updated"),
            @ApiResponse(responseCode = "400", description = "Invalid trainer assignment request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found"),
            @ApiResponse(responseCode = "409", description = "Assignment conflicts with current trainee state"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TrainerDto>> updateTraineeTrainers(
            @RequestBody TraineeTrainerAssignmentRequestDto trainerList,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        log.info("X-Correlation-Id: {}", correlationId);

        List<TrainerDto> trainerDtoList = traineeFacade.updateTraineeTrainers(
                trainerList.getUsername(),
                trainerList.getTrainerUsernames()
        );

        return ResponseEntity.status(HttpStatus.OK).body(trainerDtoList);
    }


    @GetMapping("/profile/trainings")
    @Operation(summary = "Get trainee training list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee training list returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request filters"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TraineeTrainingDto>> getTraineeTrainingsList(
            @RequestParam("traineeUsername") String traineeUsername,
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate,
            @RequestParam(value = "trainerUsername", required = false) String trainerUsername,
            @RequestParam(value = "trainingType", required = false) String trainingType,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ){
        log.info("X-Correlation-Id: {}", correlationId);
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
    @Operation(summary = "Update trainee active status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee active status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "409", description = "Status update conflicts with current trainee state"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> updateTraineeStatus (
            @RequestBody ActiveDto activeDto
    ) {
        if (activeDto.isActive()) {
            traineeFacade.activateTrainee(activeDto.getUsername());
        } else {
            traineeFacade.deactivateTrainee(activeDto.getUsername());

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
