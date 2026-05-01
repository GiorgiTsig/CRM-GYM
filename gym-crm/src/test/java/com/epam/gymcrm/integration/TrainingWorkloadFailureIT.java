package com.epam.gymcrm.integration;

import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.integration.dto.request.TokenDto;
import com.epam.gymcrm.integration.steps.TestContext;
import com.epam.gymcrm.repository.TrainingRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TrainingWorkloadFailureIT {
    private static final String TRAINER_USERNAME = "john.trainer";
    private static final String TRAINEE_USERNAME = "missing.trainee";
    private static final String TRAINING_NAME = "Morning Training";
    private static final int TRAINING_DURATION = 60;

    private ResponseEntity<String> response;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TestContext context;


    private TrainingRequestDto validTrainingRequest() {
        TrainingRequestDto request = new TrainingRequestDto();
        request.setTrainerUsername(TRAINER_USERNAME);
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

    @When("I send POST request to {string} with non-existing trainee username")
    public void sendPostRequestWithNonExistingTraineeUsername(String url) {
        String token = requestToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<TrainingRequestDto> request = new HttpEntity<>(validTrainingRequest(), headers);
        response = testRestTemplate.postForEntity(url, request, String.class);
        context.setResponse(response);
    }

    @And("the training should not be saved in the database")
    public void trainingShouldNotBeSavedInDatabase() {
        var training = trainingRepository.findByTraineeUserUsername(TRAINEE_USERNAME);
        assertThat(training.isEmpty()).isTrue();
    }
}
