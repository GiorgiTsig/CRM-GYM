package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.facade.TrainingTypesFacade;
import com.epam.gymcrm.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/training")
@Tag(name = "Training", description = "Operations for training creation and training type retrieval")
public class TrainingController {

    private TrainingFacade trainingFacade;
    private TrainingTypesFacade trainingTypesFacade;
    private AuthenticationUtil authentication;

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @Autowired
    public void setTrainingTypesFacade(TrainingTypesFacade trainingTypesFacade) {
        this.trainingTypesFacade = trainingTypesFacade;
    }

    @Autowired
    public void setAuthentication(AuthenticationUtil authentication) {
        this.authentication = authentication;
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
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestBody TrainingRequestDto trainingRequestDto
    ) {
        authentication.auth(authUsername, authPassword);
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
    ResponseEntity<List<TrainingTypeDetailsDto>> getTrainingType(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword
    ) {
        authentication.auth(authUsername, authPassword);
        List<TrainingTypeDetailsDto> trainingTypesDto =  trainingTypesFacade.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(trainingTypesDto);
    }

}
