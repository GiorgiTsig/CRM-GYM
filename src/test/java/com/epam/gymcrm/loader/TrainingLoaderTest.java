package com.epam.gymcrm.loader;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.TraineeService;
import com.epam.gymcrm.service.TrainerService;
import com.epam.gymcrm.service.TrainingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingLoaderTest {

    @Mock
    private TrainingService trainingService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TrainingLoader trainingLoader;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ReflectionTestUtils.setField(trainingLoader, "objectMapper", mapper);
    }

    @Test
    void processesTrainingsWhenBothUsersExist() throws IOException {
        String json = """
                { "trainings": [
                    {
                      "name": "Session",
                      "date": "2024-01-01",
                      "duration": 45,
                      "trainer": { "user": { "username": "trainer" } },
                      "trainee": { "user": { "username": "trainee" } }
                    }
                ] }
                """;
        JsonNode root = new ObjectMapper().readTree(json);
        Trainer trainer = new Trainer();
        trainer.setUser(new User("trainer", true));
        Trainee trainee = new Trainee();
        trainee.setUser(new User("trainee", true));
        when(trainerService.getTrainer("trainer")).thenReturn(Optional.of(trainer));
        when(traineeService.getTrainee("trainee")).thenReturn(Optional.of(trainee));

        trainingLoader.processData(root);

        ArgumentCaptor<String> trainerUser = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> traineeUser = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);
        verify(trainingService).createTraining(trainerUser.capture(), traineeUser.capture(), trainingCaptor.capture());
        assertEquals("trainer", trainerUser.getValue());
        assertEquals("trainee", traineeUser.getValue());
        assertEquals("Session", trainingCaptor.getValue().getName());
        assertEquals(LocalDate.parse("2024-01-01"), trainingCaptor.getValue().getDate());
        assertEquals(45, trainingCaptor.getValue().getDuration());
    }

    @Test
    void skipsTrainingWhenEitherSideMissing() throws IOException {
        String json = """
                { "trainings": [
                    {
                      "name": "Session",
                      "date": "2024-01-01",
                      "duration": 45,
                      "trainer": { "user": { "username": "trainer" } },
                      "trainee": { "user": { "username": "trainee" } }
                    }
                ] }
                """;
        JsonNode root = new ObjectMapper().readTree(json);
        when(trainerService.getTrainer("trainer")).thenReturn(Optional.empty());
        when(traineeService.getTrainee("trainee")).thenReturn(Optional.of(new Trainee()));

        trainingLoader.processData(root);

        verifyNoInteractions(trainingService);
    }

    @Test
    void skipsWhenNoTrainingsArray() throws IOException {
        JsonNode root = new ObjectMapper().readTree("{\"other\":[]}");

        trainingLoader.processData(root);

        verifyNoInteractions(trainingService);
    }
}
