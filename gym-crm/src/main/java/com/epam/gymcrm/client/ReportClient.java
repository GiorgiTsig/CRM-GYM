package com.epam.gymcrm.client;

import com.epam.gymcrm.dto.training.TrainingEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "training-report-service")
public interface ReportClient {

    @PostMapping("/trainer-workload")
    void sendWorkload(
              @RequestBody TrainingEventDto trainingDto,
              @RequestHeader("X-Correlation-Id") String correlationId
    );
}
