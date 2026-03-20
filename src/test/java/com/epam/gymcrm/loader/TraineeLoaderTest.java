package com.epam.gymcrm.loader;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.service.TraineeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TraineeLoaderTest {

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeLoader traineeLoader;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(traineeLoader, "objectMapper", new ObjectMapper());
    }

    @Test
    void processesTraineesArrayAndCreatesProfiles() throws IOException {
        String json = """
                { "trainees": [
                    { "address": "Street 1",
                      "user": { "firstName": "John", "lastName": "Smith", "active": true }
                    }
                ] }
                """;
        JsonNode root = new ObjectMapper().readTree(json);

        traineeLoader.processData(root);

        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeService).createTraineeProfile(traineeCaptor.capture());
        Trainee capturedTrainee = traineeCaptor.getValue();
        assertEquals("John", capturedTrainee.getUser().getFirstName());
        assertEquals("Smith", capturedTrainee.getUser().getLastName());
        assertTrue(capturedTrainee.getUser().isActive());
        assertEquals("Street 1", capturedTrainee.getAddress());
    }

    @Test
    void skipsWhenNoTraineesArray() throws IOException {
        JsonNode root = new ObjectMapper().readTree("{\"other\":[]}");

        traineeLoader.processData(root);

        verifyNoInteractions(traineeService);
    }
}
