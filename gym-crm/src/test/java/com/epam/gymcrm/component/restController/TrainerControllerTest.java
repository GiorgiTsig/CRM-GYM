package com.epam.gymcrm.component.restController;

import com.epam.gymcrm.dto.auth.request.ActiveDto;
import com.epam.gymcrm.dto.auth.response.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.request.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.request.TrainerProfileUpdateRequestDto;
import com.epam.gymcrm.dto.trainer.response.TrainerProfileDto;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.facade.TrainerFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


public class TrainerControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainerFacade trainerFacade;

    private MvcResult result;

    private CreateTrainerDto request;

    private String trainerUsername;


    @Given("a user provides valid trainer information")
    public void valid_trainer_data() {
        request = new CreateTrainerDto();
        request.setFirstName("John");
        request.setLastName("Doe");

        AuthenticationDto response = new AuthenticationDto();
        response.setUsername("john");
        response.setPassword("password");

        given(trainerFacade.createTrainerProfile(any())).willReturn(response);
    }

    @Given("a trainer already exists")
    public void trainer_exists_conflict() {
        request = new CreateTrainerDto();
        request.setFirstName("John");
        request.setLastName("Doe");

        given(trainerFacade.createTrainerProfile(any()))
                .willThrow(new IllegalStateException("Already exists"));
    }

    @When("the user submits a trainer creation request")
    public void create_trainer() throws Exception {
        result = mockMvc.perform(post("/trainer/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Then("the trainer profile is created")
    public void verify_created() {
        assertThat(result.getResponse().getStatus()).isEqualTo(201);

        then(trainerFacade).should().createTrainerProfile(any());
    }

    @Then("the system returns trainer credentials")
    public void verify_credentials() throws Exception {
        String body = result.getResponse().getContentAsString();

        assertThat(body).contains("john");
    }

    @Then("the system rejects trainer creation")
    public void verify_conflict() {
        assertThat(result.getResponse().getStatus()).isEqualTo(409);
    }

    @Given("a trainer exists in the system")
    public void trainer_exists() {
        trainerUsername = "john.doe";

        TrainerProfileDto dto = new TrainerProfileDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSpecialization("Fitness");
        dto.setActive(true);

        given(trainerFacade.getTrainerProfile(trainerUsername)).willReturn(dto);
    }

    @Given("a trainer does not exist")
    public void trainer_not_exists() {
        trainerUsername = "unknown";

        given(trainerFacade.getTrainerProfile(trainerUsername))
                .willThrow(new EntityNotFoundException("Not found"));
    }

    @When("the user requests the trainer profile")
    public void get_profile() throws Exception {
        result = mockMvc.perform(get("/trainer/profile")
                        .param("trainerProfile", trainerUsername))
                .andReturn();
    }

    @Then("the system returns the trainer profile")
    public void verify_profile() throws Exception {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        String body = result.getResponse().getContentAsString();
        assertThat(body)
                .contains("John")
                .contains("Doe")
                .contains("Fitness");

        then(trainerFacade).should().getTrainerProfile(trainerUsername);
    }

    @Then("the system returns trainer not found error")
    public void verify_not_found() {
        assertThat(result.getResponse().getStatus()).isEqualTo(404);

        then(trainerFacade).should().getTrainerProfile(trainerUsername);
    }

    @Given("a trainer with existing profile")
    public void trainer_with_existing_profile() {
        trainerUsername = "john.doe";
    }

    @When("the user updates the trainer profile")
    public void update_profile() throws Exception {
        TrainerProfileUpdateRequestDto request = new TrainerProfileUpdateRequestDto();
        request.setUsername(trainerUsername);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setActive(true);

        TrainerProfileDto response = new TrainerProfileDto();
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setSpecialization("Fitness");
        response.setActive(true);

        given(trainerFacade.updateTrainerProfile(any(), any(), any(), anyBoolean()))
                .willReturn(response);

        result = mockMvc.perform(put("/trainer/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Then("the trainer profile is updated")
    public void verify_update() {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        then(trainerFacade).should()
                .updateTrainerProfile(any(), any(), any(), anyBoolean());
    }

    @Given("a trainer is inactive")
    public void trainer_inactive() {
        trainerUsername = "john.doe";
    }

    @When("the user activates the trainer")
    public void activate() throws Exception {
        ActiveDto dto = new ActiveDto();
        dto.setUsername(trainerUsername);
        dto.setActive(true);

        result = mockMvc.perform(patch("/trainer/profile/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
    }

    @Then("the trainer becomes active")
    public void verify_active() {
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        then(trainerFacade).should().activateTrainer(trainerUsername);
    }
}
