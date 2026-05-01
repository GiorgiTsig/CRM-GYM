package com.epam.gymcrm.component.restController;

import com.epam.gymcrm.dto.auth.request.ActiveDto;
import com.epam.gymcrm.dto.auth.response.AuthenticationDto;
import com.epam.gymcrm.dto.trainee.request.CreateTraineeDto;
import com.epam.gymcrm.dto.trainee.request.TraineeUpdateRequestDto;
import com.epam.gymcrm.dto.trainee.response.TraineeProfileDto;
import com.epam.gymcrm.dto.trainee.response.TrainerDto;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.facade.TraineeFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TraineeFacade traineeFacade;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateTraineeDto request;

    private MvcResult result;

    private String traineeUsername;

    @Given("a user with valid trainee information")
    public void valid_data() {
        request = new CreateTraineeDto();
        request.setFirstName("John");
        request.setLastName("Doe");

        AuthenticationDto auth = new AuthenticationDto();
        auth.setUsername("john.doe");
        auth.setPassword("pass123");

        given(traineeFacade.createTraineeProfile(any(CreateTraineeDto.class))).willReturn(auth);
    }

    @Given("a user with invalid trainee information")
    public void invalid_data() {
        request = new CreateTraineeDto();
        request.setFirstName(null);
        request.setLastName(null);
    }


    @When("the user sends a request to create a trainee profile")
    public void send_request() throws Exception {
        result = mockMvc.perform(post("/trainee/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }


    @Then("the trainee profile is created")
    public void created() {
        assertThat(result.getResponse().getStatus()).isEqualTo(201);
        then(traineeFacade).should().createTraineeProfile(any());
    }

    @Then("the response contains the trainee username")
    public void contains_username() throws Exception {
        String body = result.getResponse().getContentAsString();

        assertThat(body)
                .isNotBlank()
                .contains("john.doe");
    }

    @Then("the trainee profile is not created")
    public void not_created() {
        assertThat(result.getResponse().getStatus()).isEqualTo(400);

        then(traineeFacade).shouldHaveNoInteractions();
    }

    @Then("the response contains a validation error")
    public void validation_error() throws Exception {
        String body = result.getResponse().getContentAsString();

        assertThat(body)
                .isNotBlank()
                .contains("must not be blank");
    }

    @Given("a trainee exists in the system")
    public void trainee_exists() {
        traineeUsername = "john.doe";

        TraineeProfileDto dto = new TraineeProfileDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        given(traineeFacade.getTraineeProfile(traineeUsername)).willReturn(dto);
    }

    @Given("a trainee does not exist in the system")
    public void trainee_not_exists() {
        traineeUsername = "unknown";

        given(traineeFacade.getTraineeProfile(traineeUsername))
                .willThrow(new EntityNotFoundException("Not found"));
    }

    @When("the user requests the trainee profile")
    public void get_profile() throws Exception {
        result = mockMvc.perform(get("/trainee/profile")
                        .param("traineeProfile", traineeUsername))
                .andReturn();
    }

    @Then("the system returns the trainee profile")
    public void verify_profile() throws Exception {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        String body = result.getResponse().getContentAsString();
        assertThat(body)
                .contains("John")
                .contains("Doe");

        then(traineeFacade).should().getTraineeProfile(traineeUsername);
    }

    @Then("the system returns not found error")
    public void verify_not_found() {
        assertThat(result.getResponse().getStatus()).isEqualTo(404);

        then(traineeFacade).should().getTraineeProfile(traineeUsername);
    }

    @Given("a trainee with existing profile")
    public void trainee_with_existing_profile() {
        traineeUsername = "john.doe";
    }

    @When("the user updates the trainee profile with valid data")
    public void update_profile() throws Exception {
        TraineeUpdateRequestDto request = new TraineeUpdateRequestDto();
        request.setUsername(traineeUsername);
        request.setFirstName("John");
        request.setLastName("Doe");

        TraineeProfileDto response = new TraineeProfileDto();
        response.setFirstName("John");
        response.setLastName("Doe");

        given(traineeFacade.updateTraineeProfile(any(), any(), any(), any(), any(), anyBoolean()))
                .willReturn(response);

        result = mockMvc.perform(put("/trainee/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Then("the system updates the trainee profile")
    public void verify_update() {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        then(traineeFacade).should()
                .updateTraineeProfile(any(), any(), any(), any(), any(), anyBoolean());
    }

    @When("the user deletes the trainee profile")
    public void delete_profile() throws Exception {
        result = mockMvc.perform(delete("/trainee/profile")
                        .param("traineeUsername", traineeUsername))
                .andReturn();
    }

    @Then("the trainee profile is removed")
    public void verify_delete() {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        then(traineeFacade).should().deleteTrainee(traineeUsername);
    }


    @When("the user requests unassigned trainers")
    public void get_trainers() throws Exception {
        given(traineeFacade.getUnassignedTrainersForTrainee(traineeUsername))
                .willReturn(List.of(new TrainerDto()));

        result = mockMvc.perform(get("/trainee/{username}/unassigned-traineers", traineeUsername))
                .andReturn();
    }

    @Then("the system returns list of trainers")
    public void verify_trainers() {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }


    @Given("a trainee is inactive")
    public void trainee_inactive() {
        traineeUsername = "john.doe";
    }

    @When("the user activates the trainee")
    public void activate() throws Exception {
        ActiveDto dto = new ActiveDto();
        dto.setUsername(traineeUsername);
        dto.setActive(true);

        result = mockMvc.perform(patch("/trainee/profile/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
    }

    @Then("the trainee becomes active")
    public void verify_active() {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        then(traineeFacade).should().activateTrainee(traineeUsername);
    }
}
