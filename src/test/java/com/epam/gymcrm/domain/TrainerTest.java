package com.epam.gymcrm.domain;

import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class TrainerTest {

    @Test
    void shouldSetAndGetTrainees() {
        Trainer trainer = new Trainer();

        List<Trainee> trainees = new ArrayList<>();
        trainees.add(new Trainee());

        trainer.setTrainees(trainees);

        assertEquals(trainees, trainer.getTrainees());
    }

    @Test
    void shouldReturnId() {
        UUID id = UUID.randomUUID();
        User user = new User();

        Trainer trainer = new Trainer();

        trainer.setId(id);
        trainer.setUser(user);

        assertEquals(id, trainer.getId());
    }

}
