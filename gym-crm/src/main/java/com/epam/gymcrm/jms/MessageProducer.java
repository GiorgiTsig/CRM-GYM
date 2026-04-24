package com.epam.gymcrm.jms;

import com.epam.gymcrm.dto.training.TrainingEventDto;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    private final JmsTemplate jmsTemplate;
    private final String workloadQueue;

    public MessageProducer(
            JmsTemplate jmsTemplate,
            @Value("${app.messaging.workload-queue}") String workloadQueue
    ) {
        this.jmsTemplate = jmsTemplate;
        this.workloadQueue = workloadQueue;
    }

    public void send(TrainingEventDto dto) {
        String correlationId = MDC.get("correlationId");
        jmsTemplate.convertAndSend(workloadQueue, dto, message -> {
            if (!correlationId.isBlank()) {
                message.setJMSCorrelationID(correlationId);
            }
            return message;
        });
    }

}
