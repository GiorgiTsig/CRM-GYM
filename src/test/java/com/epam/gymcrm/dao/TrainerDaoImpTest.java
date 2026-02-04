package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.storage.TrainersStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoImpTest {

    private TrainersStorage trainersStorage;
    private TrainerDaoImp daoImp;

    @BeforeEach
    void setUp() {
        trainersStorage = new TrainersStorage();
        daoImp = new TrainerDaoImp();
        daoImp.setTrainersStorage(trainersStorage);
    }

    private Trainer buildTrainer(Long id) {
        User user = new User(id, "John", "Wick", ("user" + id),  "password1", true);

        Trainer trainer = new Trainer(
                user,
                "Yoga"
        );

        return trainer;
    }

    @Test
    void get() {
        Trainer trainee = buildTrainer(1L);
        trainersStorage.getTrainers().put(1L, trainee);

        Optional<Trainer> result = daoImp.get(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void save() {
        Trainer trainee = buildTrainer(2L);

        daoImp.save(trainee);

        assertTrue(trainersStorage.getTrainers().containsKey(2L));
        assertEquals(trainee, trainersStorage.getTrainers().get(2L));
    }

    @Test
    void update() {
        Trainer original = buildTrainer(3L);
        trainersStorage.getTrainers().put(3L, original);

        Trainer updated = buildTrainer(3L);
        updated.setSpecialization("MMA");

        daoImp.update(updated);

        assertEquals("MMA", trainersStorage.getTrainers().get(3L).getSpecialization());
    }

    @Test
    void delete() {
        Trainer trainee = buildTrainer(4L);
        trainersStorage.getTrainers().put(4L, trainee);

        daoImp.delete(4L);

        assertFalse(trainersStorage.getTrainers().containsKey(4L));
    }

    @Test
    void delete_NonExistingTrainer_ShouldNotThrowException() {
        assertDoesNotThrow(() -> daoImp.delete(999L));
    }

    @Test
    void getAll_EmptyStorage_ShouldReturnEmptyMap() {
        Map<Long, Trainer> result = daoImp.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_WithData_ShouldReturnAllTrainers() {
        Trainer trainer1 = buildTrainer(1L);
        Trainer trainer2 = buildTrainer(2L);
        Trainer trainer3 = buildTrainer(3L);

        trainersStorage.getTrainers().put(1L, trainer1);
        trainersStorage.getTrainers().put(2L, trainer2);
        trainersStorage.getTrainers().put(3L, trainer3);

        Map<Long, Trainer> result = daoImp.getAll();

        assertEquals(3, result.size());
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));
        assertTrue(result.containsKey(3L));
    }

    @Test
    void getAll_ShouldReturnImmutableCopy() {
        Trainer trainer = buildTrainer(1L);
        trainersStorage.getTrainers().put(1L, trainer);

        Map<Long, Trainer> result = daoImp.getAll();

        assertThrows(UnsupportedOperationException.class, () -> {
            result.put(2L, buildTrainer(2L));
        });
    }

    @Test
    void update_NonExistingTrainer_ShouldThrowException() {
        Trainer trainer = buildTrainer(999L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            daoImp.update(trainer);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }
}