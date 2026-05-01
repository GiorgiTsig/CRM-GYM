package com.epam.gymcrm.component.restController;

import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.facade.TrainingTypesFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


public class TrainingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainingFacade trainingFacade;

    @Autowired
    private TrainingTypesFacade trainingTypesFacade;

    private MvcResult result;

    private TrainingRequestDto request;


    @Given("a valid training request")
    public void valid_training_request() {
        request = new TrainingRequestDto();
        request.setTraineeUsername("john.doe");
    }

    @Given("an invalid training request")
    public void invalid_training_request() {
        request = new TrainingRequestDto();

        willThrow(new IllegalArgumentException("Invalid training request"))
                .given(trainingFacade)
                .addTraining(any());
    }

    @When("the user submits a training creation request")
    public void add_training() throws Exception {
        result = mockMvc.perform(post("/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Then("the training is added")
    public void verify_added() {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        then(trainingFacade).should().addTraining(any());
    }

    @Then("the system rejects training creation")
    public void verify_invalid_training() {
        assertThat(result.getResponse().getStatus()).isEqualTo(400);

        then(trainingFacade).should().addTraining(any());
    }

    @Given("training types exist in the system")
    public void training_types_exist() {
        given(trainingTypesFacade.findAll())
                .willReturn(List.of(new TrainingTypeDetailsDto()));
    }

    @When("the user requests training types")
    public void get_types() throws Exception {
        result = mockMvc.perform(get("/training/types"))
                .andReturn();
    }

    @Then("the system returns training types list")
    public void verify_types() {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        then(trainingTypesFacade).should().findAll();
    }


    @Given("training does not exist")
    public void training_not_found() {
        willThrow(new EntityNotFoundException("Not found"))
                .given(trainingFacade)
                .deleteTraining("john");
    }

    @When("the user deletes the training")
    public void delete_training() throws Exception {
        result = mockMvc.perform(delete("/training")
                        .param("traineeUsername", "john"))
                .andReturn();
    }

    @Then("the training is removed")
    public void verify_deleted() {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        then(trainingFacade).should().deleteTraining("john");
    }

    @Then("the system returns training not found error")
    public void verify_delete_not_found() {
        assertThat(result.getResponse().getStatus()).isEqualTo(404);

        then(trainingFacade).should().deleteTraining("john");
    }
}
