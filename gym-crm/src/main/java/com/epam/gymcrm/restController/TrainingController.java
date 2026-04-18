package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.facade.TrainingTypesFacade;
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

import java.util.List;

@RestController
@RequestMapping(value = "/training")
@Tag(name = "Training", description = "Operations for training creation and training type retrieval")
public class TrainingController {

    private static final Logger log = LoggerFactory.getLogger(TrainingController.class);
    private TrainingFacade trainingFacade;
    private TrainingTypesFacade trainingTypesFacade;

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @Autowired
    public void setTrainingTypesFacade(TrainingTypesFacade trainingTypesFacade) {
        this.trainingTypesFacade = trainingTypesFacade;
    }

    @PostMapping
    @Operation(summary = "Add training")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Related trainee or trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> addTraining(
            @RequestBody TrainingRequestDto trainingRequestDto
    ) {
        trainingFacade.addTraining(trainingRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/types")
    @Operation(summary = "Get training types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training types returned"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TrainingTypeDetailsDto>> getTrainingType() {
        List<TrainingTypeDetailsDto> trainingTypesDto =  trainingTypesFacade.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(trainingTypesDto);
    }

    @DeleteMapping
    @Operation(summary = "Delete Training")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Training not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> deleteTraining(
            @RequestParam("traineeUsername") String traineeUsername,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        log.info("X-Correlation-Id: {}", correlationId);
        trainingFacade.deleteTraining(traineeUsername);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
