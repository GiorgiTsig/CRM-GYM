package com.epam.gymcrm.health;

import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreDataHealthIndicatorTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private CoreDataHealthIndicator healthIndicator;

    @Test
    void health_returnsUp_whenCoreDataExists() {
        when(trainerRepository.count()).thenReturn(1L);
        when(traineeRepository.count()).thenReturn(1L);
        when(trainingRepository.count()).thenReturn(0L);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(1L, health.getDetails().get("trainers"));
        assertEquals(1L, health.getDetails().get("trainees"));
        assertEquals(0L, health.getDetails().get("trainings"));
    }

    @Test
    void health_returnsOutOfService_whenTrainerOrTraineeDataMissing() {
        when(trainerRepository.count()).thenReturn(0L);
        when(traineeRepository.count()).thenReturn(2L);
        when(trainingRepository.count()).thenReturn(5L);

        Health health = healthIndicator.health();

        assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
    }

    @Test
    void health_returnsDown_whenRepositoryFails() {
        when(trainerRepository.count()).thenThrow(new RuntimeException("boom"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("error"));
    }
}

