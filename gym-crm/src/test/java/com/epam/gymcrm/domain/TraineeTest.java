package com.epam.gymcrm.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeTest {


    @Test
    void shouldCreateTraineeArgsConstructor() {
        String address = "fr";
        LocalDate dateOfBirth = LocalDate.of(2026, 3, 24);

        Trainee trainee = new Trainee(dateOfBirth, address);

        assertEquals(address, trainee.getAddress());
        assertEquals(dateOfBirth, trainee.getDateOfBirth());
    }
}