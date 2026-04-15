package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.auth.request.ActiveDto;
import com.epam.gymcrm.dto.auth.response.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.response.TrainerTrainingDto;
import com.epam.gymcrm.dto.trainer.request.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.response.TrainerProfileDto;
import com.epam.gymcrm.dto.trainer.request.TrainerProfileUpdateRequestDto;
import com.epam.gymcrm.facade.TrainerFacade;
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
@RequestMapping(value = "/trainer")
@Tag(name = "Trainer", description = "Operations for trainer profile management and trainer training retrieval")
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


    @PostMapping("/profile")
    @Operation(summary = "Create trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer profile created"),
            @ApiResponse(responseCode = "400", description = "Invalid trainer data"),
            @ApiResponse(responseCode = "409", description = "Trainer profile already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<AuthenticationDto> create(
            @RequestBody CreateTrainerDto userTrainerDto
    ) {
        AuthenticationDto trainerCred = trainerFacade.createTrainerProfile(userTrainerDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainerCred);

    }

    @Operation(summary = "Get trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile returned"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/profile")
    ResponseEntity<TrainerProfileDto> getTrainerProfile(
            @RequestParam("trainerProfile") String trainerProfile,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        TrainerProfileDto trainerDto = trainerFacade.getTrainerProfile(trainerProfile);
        return ResponseEntity.status(HttpStatus.OK).body(trainerDto);
    }

    @Operation(summary = "Update trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile updated"),
            @ApiResponse(responseCode = "400", description = "Invalid update request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "409", description = "Update conflicts with current trainer state"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/profile")
    ResponseEntity<TrainerProfileDto> updateTrainerStatus(
            @RequestBody TrainerProfileUpdateRequestDto trainerRequestDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        TrainerProfileDto profileDTO = trainerFacade.updateTrainerProfile(
                trainerRequestDto.getUsername(),
                trainerRequestDto.getFirstName(),
                trainerRequestDto.getLastName(),
                trainerRequestDto.isActive()
        );

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }

    @Operation(summary = "Get trainer training list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer training list returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request filters"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/profile/trainings")
    ResponseEntity<List<TrainerTrainingDto>> getTrainerTrainingsList(
            @RequestParam("trainerUsername") String trainerUsername,
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate,
            @RequestParam(value = "traineeUsername", required = false) String traineeUsername,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ){
        log.info("TransactionId: {}", transactionId);

        List<TrainerTrainingDto> trainingDtoList = trainingFacade.getTrainerTrainings(
                trainerUsername,
                fromDate,
                toDate,
                traineeUsername
        );

        return ResponseEntity.status(HttpStatus.OK).body(trainingDtoList);
    }

    @Operation(summary = "Update trainer active status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer active status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "409", description = "Status update conflicts with current trainer state"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/profile/status")
    ResponseEntity<Void> updateTrainerProfile (
            @RequestBody ActiveDto activeDto
    ) {
        if (activeDto.isActive()) {
            trainerFacade.activateTrainer(activeDto.getUsername());
        } else  {
            trainerFacade.deactivateTrainer(activeDto.getUsername());

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
