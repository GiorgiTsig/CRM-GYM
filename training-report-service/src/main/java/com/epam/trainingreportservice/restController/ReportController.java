package com.epam.trainingreportservice.restController;

import com.epam.trainingreportservice.dto.TrainingEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @PostMapping("/trainer-workload")
    public ResponseEntity<Void> handle(
            @RequestBody TrainingEventDto trainingDto,
            @RequestHeader("X-Correlation-Id") String correlationId
    ) {
        log.info("Received workload event with correlationId={}", correlationId);
//        reportService.process(trainingDto);
        return ResponseEntity.ok().build();
    }
}
