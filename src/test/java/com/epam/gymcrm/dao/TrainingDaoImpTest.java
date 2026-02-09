package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoImpTest {

    private TrainingStorage trainingStorage;
    private TrainingDaoImp dao;

    @BeforeEach
    void setUp() {
        trainingStorage = new TrainingStorage();
        dao = new TrainingDaoImp();
        ReflectionTestUtils.setField(dao, "trainingStorage", trainingStorage);
    }

    private Training buildTraining(Long id) {
        Long trainee = new Random().nextLong();
        Long trainer = new Random().nextLong();

        Training training = new Training(
                id,
                trainee,
                trainer,
                "Morning Yoga Session",
                TrainingType.Yoga,
                "2024-01-15",
                "60 minutes"
        );

        return training;
    }

    @Test
    void get_ExistingTraining_ShouldReturnTraining() {
        Training training = buildTraining(1L);
        trainingStorage.getTraining().put(1L, training);

        Optional<Training> result = dao.get(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Morning Yoga Session", result.get().getName());
    }

    @Test
    void get_NonExistingTraining_ShouldReturnEmpty() {
        Optional<Training> result = dao.get(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void save_ShouldAddTrainingToStorage() {
        Training training = buildTraining(2L);

        dao.save(training);

        assertTrue(trainingStorage.getTraining().containsKey(2L));
        assertEquals(training, trainingStorage.getTraining().get(2L));
    }

    @Test
    void save_MultipleTrainings_ShouldStoreAll() {
        Training training1 = buildTraining(1L);
        Training training2 = buildTraining(2L);
        Training training3 = buildTraining(3L);

        dao.save(training1);
        dao.save(training2);
        dao.save(training3);

        assertEquals(3, trainingStorage.getTraining().size());
    }

    @Test
    void update_ExistingTraining_ShouldUpdateSuccessfully() {
        Training original = buildTraining(3L);
        trainingStorage.getTraining().put(3L, original);

        Training updated = buildTraining(3L);
        updated.setName("Evening Yoga Session");
        updated.setDuration("90 minutes");

        dao.update(updated);

        assertEquals("Evening Yoga Session", trainingStorage.getTraining().get(3L).getName());
        assertEquals("90 minutes", trainingStorage.getTraining().get(3L).getDuration());
    }

    @Test
    void update_NonExistingTraining_ShouldThrowException() {
        Training training = buildTraining(999L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            dao.update(training);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void delete_ExistingTraining_ShouldRemoveFromStorage() {
        Training training = buildTraining(4L);
        trainingStorage.getTraining().put(4L, training);

        dao.delete(4L);

        assertFalse(trainingStorage.getTraining().containsKey(4L));
    }

    @Test
    void delete_NonExistingTraining_ShouldNotThrowException() {
        assertDoesNotThrow(() -> dao.delete(999L));
    }

    @Test
    void getAll_EmptyStorage_ShouldReturnEmptyMap() {
        Map<Long, Training> result = dao.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_WithData_ShouldReturnAllTrainings() {
        Training training1 = buildTraining(1L);
        Training training2 = buildTraining(2L);
        Training training3 = buildTraining(3L);

        trainingStorage.getTraining().put(1L, training1);
        trainingStorage.getTraining().put(2L, training2);
        trainingStorage.getTraining().put(3L, training3);

        Map<Long, Training> result = dao.getAll();

        assertEquals(3, result.size());
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));
        assertTrue(result.containsKey(3L));
    }

    @Test
    void getAll_ShouldReturnImmutableCopy() {
        Training training = buildTraining(1L);
        trainingStorage.getTraining().put(1L, training);

        Map<Long, Training> result = dao.getAll();

        assertThrows(UnsupportedOperationException.class, () -> {
            result.put(2L, buildTraining(2L));
        });
    }
}
