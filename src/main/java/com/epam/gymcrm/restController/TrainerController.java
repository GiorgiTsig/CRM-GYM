package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.dto.trainer.TrainerTrainingsDto;
import com.epam.gymcrm.dto.trainer.TrainingDto;
import com.epam.gymcrm.dto.trainer.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.TrainerDto;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.mappper.TrainerMapper;
import jakarta.validation.ConstraintViolationException;
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
public class TrainerController {
    private static final Logger log = LoggerFactory.getLogger(TrainerController.class);
    private TrainerFacade trainerFacade;
    private TrainerMapper trainerMapper;
    private TrainingFacade trainingFacade;

    @Autowired
    public void setTrainingFacade(TrainingFacade trainingFacade) {
        this.trainingFacade = trainingFacade;
    }

    @Autowired
    public void setTrainerFacade(TrainerFacade trainerFacade) {
        this.trainerFacade = trainerFacade;
    }

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    @PostMapping("/create")
    ResponseEntity<String> create(
            @RequestBody CreateTrainerDto userTrainerDto
    ) {
        Trainer trainer = trainerMapper.toTrainer(userTrainerDto);
        Trainer createdTrainer = trainerFacade.createTrainerProfile(trainer.getUser(), trainer, trainer.getTrainingType().getTrainingTypeName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registration successful " + createdTrainer.getUser().getUsername() + " " + createdTrainer.getUser().getPassword());

    }


    @GetMapping("/get")
    ResponseEntity<TrainerDto> getTrainerProfile(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        Trainer trainer = trainerFacade.getTrainerProfile(username, password).orElseThrow();
        TrainerDto trainerDto = trainerMapper.toTrainerDto(trainer);

        return ResponseEntity.status(HttpStatus.OK).body(trainerDto);
    }

    @PutMapping("/update")
    ResponseEntity<TrainerDto> updateTraineeProfile(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader("firstName") String firstName,
            @RequestHeader("lastName") String lastName,
            @RequestHeader("Specialization") String specialization,
            @RequestHeader("isActive") boolean isActive,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        Trainer trainee = trainerFacade.updateTrainerProfile(username, password, firstName, lastName, isActive, specialization);
        TrainerDto profileDTO = trainerMapper.toTrainerDto(trainee);

        return ResponseEntity.status(HttpStatus.OK).body(profileDTO);
    }

    @GetMapping("/get/trainings")
    ResponseEntity<List<TrainingDto>> getTrainerTrainingsList(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestBody(required = false) TrainerTrainingsDto trainerTrainingsDto,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ){
        log.info("TransactionId: {}", transactionId);

        List<Training> trainings = trainingFacade.getTrainerTrainings(
                username,
                password,
                trainerTrainingsDto.getFromDate(),
                trainerTrainingsDto.getToDate(),
                trainerTrainingsDto.getTraineeName()
        );

        List<TrainingDto> trainingDtoList = trainings.stream().map(training -> trainerMapper.toTrainingDto(training)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(trainingDtoList);
    }

    @PatchMapping("/status")
    ResponseEntity<Void> updateTraineeStatus (
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader("isActive") boolean isActive
    ) {
        if (isActive) {
            trainerFacade.activateTrainer(username, password);
        }

        if (!isActive) {
            trainerFacade.deactivateTrainer(username, password);

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
