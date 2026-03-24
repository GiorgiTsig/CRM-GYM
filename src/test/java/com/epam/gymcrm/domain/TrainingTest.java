package com.epam.gymcrm.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class TrainingTest {

    @Test
    void shouldCreateTrainingUsingAllArgsConstructor() {
        TrainingType type = new TrainingType("MMA");
        UUID id = UUID.randomUUID();
        String name = "Street Fight";
        LocalDate date = LocalDate.of(2026, 3, 24);
        Integer duration = 60;

        Training training = new Training(id, name, type, date, duration);

        assertEquals(id, training.getId());
        assertEquals(name, training.getName());
        assertSame(type, training.getType());
        assertEquals(date, training.getDate());
        assertEquals(duration, training.getDuration());
    }
}
