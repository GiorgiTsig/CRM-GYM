package com.epam.trainingreportservice.util;

import com.epam.trainingreportservice.dto.request.TrainingEventDto;
import com.epam.trainingreportservice.service.TrainerSummaryService;
import jakarta.validation.Valid;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class TrainingWorkloadListener {

    private static final Logger log = LoggerFactory.getLogger(TrainingWorkloadListener.class);

    private final TrainerSummaryService trainerSummaryService;

    public TrainingWorkloadListener(TrainerSummaryService trainerSummaryService) {
        this.trainerSummaryService = trainerSummaryService;
    }


    @JmsListener(destination = "${app.messaging.workload-queue}")
    public void processOrder(@Valid TrainingEventDto trainingDto, Message message) {
        try {
            MDC.put("correlationId", message.getJMSCorrelationID());
            trainerSummaryService.updateSummary(trainingDto);
            log.info("update summary for user {}", trainingDto.getTrainerUsername());
        } catch (JMSException e) {
            log.error("error occurred while processing order", e);
            throw new RuntimeException(e);
        } finally {
            MDC.clear();
        }
    }

}
