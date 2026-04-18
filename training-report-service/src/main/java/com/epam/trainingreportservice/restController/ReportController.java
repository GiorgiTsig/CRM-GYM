package com.epam.trainingreportservice.restController;

import com.epam.trainingreportservice.dto.request.TrainingEventDto;
import com.epam.trainingreportservice.dto.response.TrainerWorkloadResponse;
import com.epam.trainingreportservice.service.TrainerSummaryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    private final TrainerSummaryService trainerSummaryService;

    public ReportController(TrainerSummaryService trainerSummaryService) {
        this.trainerSummaryService = trainerSummaryService;
    }

    @PostMapping("/trainer-workload")
    public ResponseEntity<Void> handle(
            @Valid @RequestBody TrainingEventDto trainingDto,
            @RequestHeader("X-Correlation-Id") String correlationId
    ) {
        log.info("Received workload event with correlationId={}", correlationId);
        trainerSummaryService.updateSummary(
                trainingDto.getTrainerUsername(),
                trainingDto.getFirstName(),
                trainingDto.getLastName(),
                trainingDto.isActive(),
                trainingDto.getTrainingDate(),
                trainingDto.getDuration(),
                trainingDto.getAction()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/workload")
    public ResponseEntity<List<TrainerWorkloadResponse>> get(
            @RequestParam String username
    ) {
        List<TrainerWorkloadResponse> result =
                trainerSummaryService.getTrainerByUsername(username);


        log.info("Received workload event for trainer={}", username);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
