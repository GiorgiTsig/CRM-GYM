package com.epam.trainingreportservice.restController;

import com.epam.trainingreportservice.domain.TrainerMonthlySummary;
import com.epam.trainingreportservice.dto.TrainingEventDto;
import com.epam.trainingreportservice.service.TrainerSummaryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public ResponseEntity<TrainerMonthlySummary> get(
            @RequestParam String username,
            @RequestParam int year,
            @RequestParam int monthValue
    ) {
        Optional<TrainerMonthlySummary> result =
                trainerSummaryService.getTrainerSummary(username, year, monthValue);


        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        log.info("Received workload event for trainer={}", username);
        return ResponseEntity.ok(result.get());
    }

}
