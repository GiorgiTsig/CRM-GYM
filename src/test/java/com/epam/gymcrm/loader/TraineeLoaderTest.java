package com.epam.gymcrm.loader;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeService).createTraineeProfile(userCaptor.capture(), traineeCaptor.capture());
        assertEquals("John", userCaptor.getValue().getFirstName());
        assertEquals("Smith", userCaptor.getValue().getLastName());
        assertEquals(true, userCaptor.getValue().isActive());
        assertEquals("Street 1", traineeCaptor.getValue().getAddress());
    }

    @Test
    void skipsWhenNoTraineesArray() throws IOException {
        JsonNode root = new ObjectMapper().readTree("{\"other\":[]}");

        traineeLoader.processData(root);

        verifyNoInteractions(traineeService);
    }
}
