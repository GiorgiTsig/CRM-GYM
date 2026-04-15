package com.epam.gymcrm.health;

import com.epam.gymcrm.repository.TrainingTypeRepository;
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
class TrainingTypeSeedHealthIndicatorTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeSeedHealthIndicator healthIndicator;

    @Test
    void health_returnsUp_whenTrainingTypesSeeded() {
        when(trainingTypeRepository.count()).thenReturn(4L);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(4L, health.getDetails().get("actualCount"));
        assertEquals(4L, health.getDetails().get("minExpected"));
    }

    @Test
    void health_returnsDown_whenTrainingTypesAreMissing() {
        when(trainingTypeRepository.count()).thenReturn(2L);

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Training types are not fully seeded", health.getDetails().get("reason"));
    }

    @Test
    void health_returnsDown_whenRepositoryFails() {
        when(trainingTypeRepository.count()).thenThrow(new RuntimeException("boom"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("error"));
    }
}

