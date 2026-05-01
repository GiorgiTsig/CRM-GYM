package com.epam.trainingreportservice.component.restController;

import com.epam.trainingreportservice.dto.response.TrainerWorkloadResponse;
import com.epam.trainingreportservice.service.TrainerSummaryService;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReportControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private TrainerSummaryService trainerSummaryService;

        private MvcResult result;

        @When("I send a valid trainer workload event")
        public void iSendAValidTrainerWorkloadEvent() throws Exception {
            String body = """
                {
                  "trainerUsername": "john.doe",
                  "firstName": "John",
                  "lastName": "Doe",
                  "active": true,
                  "trainingDate": "2026-05-04",
                  "duration": 60,
                  "action": "ADD"
                }
                """;

            result = mockMvc.perform(post("/trainer-workload")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Correlation-Id", "test-correlation-id")
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();

            verify(trainerSummaryService).updateSummary(any());
        }

        @Given("trainer workload exists for username {string}")
        public void trainerWorkloadExistsForUsername(String username) {
            TrainerWorkloadResponse response = new TrainerWorkloadResponse();
            response.setTrainerUsername(username);
            response.setFirstName("John");
            response.setLastName("Doe");
            response.setStatus(true);

            when(trainerSummaryService.getTrainerByUsername(username))
                    .thenReturn(response);
        }

        @Given("trainer workload does not exist for username {string}")
        public void trainerWorkloadDoesNotExistForUsername(String username) {
            when(trainerSummaryService.getTrainerByUsername(username))
                    .thenReturn(null);
        }

        @When("I request workload for username {string}")
        public void iRequestWorkloadForUsername(String username) throws Exception {
            result = mockMvc.perform(get("/workload")
                            .param("username", username))
                    .andReturn();
        }

        @When("I request workload with blank username")
        public void iRequestWorkloadWithBlankUsername() throws Exception {
            result = mockMvc.perform(get("/workload")
                            .param("username", ""))
                    .andReturn();
        }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assert result.getResponse().getStatus() == status;
    }

    @Then("the response should contain trainer username {string}")
    public void theResponseShouldContainTrainerUsername(String username) throws Exception {
        String body = result.getResponse().getContentAsString();
        assert body.contains("\"trainerUsername\":\"" + username + "\"");
    }
}