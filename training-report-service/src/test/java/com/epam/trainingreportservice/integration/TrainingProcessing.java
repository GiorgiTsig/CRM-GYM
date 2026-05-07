package com.epam.trainingreportservice.integration;

import com.epam.trainingreportservice.domain.TrainerMonth;
import com.epam.trainingreportservice.dto.request.ActionType;
import com.epam.trainingreportservice.dto.request.TrainingEventDto;
import com.epam.trainingreportservice.repository.TrainerRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

public class TrainingProcessing {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TrainerRepository repository;

    @Value("${app.messaging.workload-queue}")
    private String queue;

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    @Given("a valid workload event is sent to the workload queue {string}")
    public void sendValidEvent(String trainerUsername) {
        var validEvent = new TrainingEventDto();
        validEvent.setTrainerUsername(trainerUsername);
        validEvent.setFirstName("firstName");
        validEvent.setLastName("lastName");
        validEvent.setActive(true);
        validEvent.setTrainingDate(LocalDate.now());
        validEvent.setDuration(60);
        validEvent.setAction(ActionType.ADD);

        jmsTemplate.convertAndSend(queue, validEvent);
    }

    @When("training-report-service processes the queue message {string}")
    public void trainingReportServiceProcessesTheQueueMessage(String trainerUsername) {
        await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> repository.findByTrainerUsername(trainerUsername).isPresent());
    }

    @Then("trainer workload should be updated in the database {string}")
    public void trainerWorkloadShouldBeUpdatedInTheDatabase(String trainerUsername) {
        var workload = repository.findByTrainerUsername(trainerUsername);
        assertThat(workload).isPresent();

        int totalDuration = workload.stream()
                .flatMap(work -> work.getYears().stream())
                .flatMap(year -> year.getMonths().stream())
                .mapToInt(TrainerMonth::getTrainingsSummaryDuration)
                .sum();

        assertThat(totalDuration).isEqualTo(60);
    }

    @Given("an invalid workload event is sent to the workload queue")
    public void anInvalidWorkloadEventIsSentToTheWorkloadQueue() {
        var invalidEvent = new TrainingEventDto();
        invalidEvent.setTrainerUsername(null);
        jmsTemplate.convertAndSend(queue, invalidEvent);
    }

    @When("training-report-service processes the queue message")
    public void processMessage() {

    }

    @Then("trainer workload for {string} should not be updated")
    public void verifyNotUpdated(String username) {
        await()
                .during(3, TimeUnit.SECONDS)
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var workload = repository.findByTrainerUsername(username);
                    assertThat(workload).isEmpty();
                });
    }
}
