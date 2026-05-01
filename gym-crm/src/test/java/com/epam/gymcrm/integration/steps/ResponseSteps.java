package com.epam.gymcrm.integration.steps;

import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseSteps {

    @Autowired
    private TestContext context;

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int status) {
        assertEquals(status, context.getResponse().getStatusCode().value());
    }
}
