package com.epam.gymcrm.loader;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.TrainerService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainersLoaderTest {

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainersLoader trainersLoader;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(trainersLoader, "objectMapper", new ObjectMapper());
    }

    @Test
    void processesTrainersArrayAndCreatesProfiles() throws IOException {
        String json = """
                { "trainers": [
                    { "trainingType": { "trainingTypeName": "Cardio" },
                      "user": { "firstName": "Jane", "lastName": "Doe", "active": true }
                    }
                ] }
                """;
        JsonNode root = new ObjectMapper().readTree(json);

        trainersLoader.processData(root);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);

        verify(trainerService).createTrainerProfile(userCaptor.capture(), trainerCaptor.capture(), typeCaptor.capture());
        assertEquals("Jane", userCaptor.getValue().getFirstName());
        assertEquals("Doe", userCaptor.getValue().getLastName());
        assertEquals(true, userCaptor.getValue().isActive());
        assertEquals("Cardio", typeCaptor.getValue());
    }

    @Test
    void skipsWhenNoTrainersArray() throws IOException {
        JsonNode root = new ObjectMapper().readTree("{\"other\":[]}");

        trainersLoader.processData(root);

        verifyNoInteractions(trainerService);
    }
}
