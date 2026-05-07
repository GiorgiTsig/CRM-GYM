package com.epam.gymcrm.integration;

import com.epam.gymcrm.domain.*;
import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.integration.dto.request.TokenDto;
import com.epam.gymcrm.integration.steps.TestContext;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.util.PasswordGenerator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TrainingWorkloadIT {
    private static final String TRAINER_USERNAME = "john.trainer";
    private static final String TRAINEE_USERNAME = "john.trainee";
    private static final String TRAINING_NAME = "Morning Training";
    private static final int TRAINING_DURATION = 60;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TestContext context;


    @Given("a trainee with username {string} exists")
    public void traineeExists(String username) {
        User user = new User();
        Trainee trainee = new Trainee();
        user.setFirstName("John");
        user.setLastName("Trainee");

        var password = passwordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(password));

        user.setUsername(username);
        user.setActive(true);
        trainee.setUser(user);

        traineeRepository.save(trainee);
    }

    @When("I send POST request to {string} with valid training data")
    public void sendPostRequestWithValidTrainingData(String url) {
        String token = requestToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<TrainingRequestDto> request = new HttpEntity<>(validTrainingRequest(), headers);
        ResponseEntity<String> response = testRestTemplate.postForEntity(url, request, String.class);
        context.setResponse(response);
    }

    private TrainingRequestDto validTrainingRequest() {
        TrainingRequestDto request = new TrainingRequestDto();
        request.setTrainerUsername(TRAINER_USERNAME);
        request.setTraineeUsername(TRAINEE_USERNAME);
        request.setName(TRAINING_NAME);
        request.setDate(LocalDate.now());
        request.setDuration(TRAINING_DURATION);

        return request;
    }

    private String requestToken() {
        ResponseEntity<TokenDto> tokenResponse = testRestTemplate.postForEntity(
                "/auth/token",
                formLoginRequest(),
                TokenDto.class
        );

        assert tokenResponse.getBody() != null;
        return tokenResponse.getBody().getToken();
    }

    private HttpEntity<MultiValueMap<String, String>> formLoginRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", context.getUsername());
        body.add("password", context.getPassword());

        return new HttpEntity<>(body, headers);
    }

    @And("the training should be saved in gym-crm database")
    public void trainingShouldBeSavedInDatabase() {
        List<Training> trainingOpt = trainingRepository.findByTraineeUserUsername(TRAINEE_USERNAME);

        assertFalse(trainingOpt.isEmpty(), "Training was not saved");

        Training saved = trainingOpt.stream().findFirst().get();

        assertEquals(TRAINER_USERNAME, saved.getTrainer().getUser().getUsername());
        assertEquals(TRAINEE_USERNAME, saved.getTrainee().getUser().getUsername());
    }
}
