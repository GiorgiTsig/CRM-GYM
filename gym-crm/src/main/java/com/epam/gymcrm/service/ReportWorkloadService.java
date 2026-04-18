package com.epam.gymcrm.service;

import com.epam.gymcrm.client.ReportClient;
import com.epam.gymcrm.dto.training.TrainingEventDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ReportWorkloadService {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ReportWorkloadService.class);
    private final ReportClient reportClient;

    public ReportWorkloadService(ReportClient reportClient) {
        this.reportClient = reportClient;
    }

    @CircuitBreaker(name = "training-report-service", fallbackMethod = "fallback")
    public void sendWorkloadSafe(TrainingEventDto dto) {
        reportClient.sendWorkload(dto);
    }

    public void fallback(TrainingEventDto dto, Throwable ex) {
        log.error("Report service failed for trainer={} action={}",
                dto.getTrainerUsername(),
                dto.getAction(),
                ex);
    }
}