package com.epam.gymcrm.client;

import com.epam.gymcrm.dto.training.TrainingEventDto;
import com.epam.gymcrm.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "training-report-service", configuration = FeignConfig.class)
public interface ReportClient {

    @PostMapping("/trainer-workload")
    void sendWorkload(@RequestBody TrainingEventDto trainingDto);
}
