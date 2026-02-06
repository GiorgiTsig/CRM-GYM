package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.storage.TraineeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoImpTest {

    private TraineeStorage traineeStorage;
    private TraineeDaoImp dao;

    @BeforeEach
    void setUp() {
        traineeStorage = new TraineeStorage();
        dao = new TraineeDaoImp();
        ReflectionTestUtils.setField(dao, "traineeStorage", traineeStorage);
    }

    private Trainee buildTrainee(Long id) {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        String formattedString = localDate.format(formatter);

        User user = new User(id, "John", "Wick", ("user" + id),  "password1", true);

        Trainee trainee = new Trainee(
                formattedString,
                "Address",
                user
        );

        trainee.setId(id);
        return trainee;
    }


    @Test
    void get() {
        Trainee trainee = buildTrainee(1L);
        traineeStorage.getTrainees().put(1L, trainee);

        Optional<Trainee> result = dao.get(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void save() {
        Trainee trainee = buildTrainee(2L);

        dao.save(trainee);

        assertTrue(traineeStorage.getTrainees().containsKey(2L));
        assertEquals(trainee, traineeStorage.getTrainees().get(2L));
    }

    @Test
    void update() {
        Trainee original = buildTrainee(3L);
        traineeStorage.getTrainees().put(3L, original);

        Trainee updated = buildTrainee(3L);
        updated.setAddress("New Address");

        dao.update(updated);

        assertEquals("New Address",
                traineeStorage.getTrainees().get(3L).getAddress());
    }

    @Test
    void delete() {
        Trainee trainee = buildTrainee(4L);
        traineeStorage.getTrainees().put(4L, trainee);

        dao.delete(4L);

        assertFalse(traineeStorage.getTrainees().containsKey(4L));
    }

    @Test
    void delete_NonExistingTrainee_ShouldNotThrowException() {
        assertDoesNotThrow(() -> dao.delete(999L));
    }

    @Test
    void getAll_EmptyStorage_ShouldReturnEmptyMap() {
        Map<Long, Trainee> result = dao.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_WithData_ShouldReturnAllTrainees() {
        Trainee trainee1 = buildTrainee(1L);
        Trainee trainee2 = buildTrainee(2L);
        Trainee trainee3 = buildTrainee(3L);

        traineeStorage.getTrainees().put(1L, trainee1);
        traineeStorage.getTrainees().put(2L, trainee2);
        traineeStorage.getTrainees().put(3L, trainee3);

        Map<Long, Trainee> result = dao.getAll();

        assertEquals(3, result.size());
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));
        assertTrue(result.containsKey(3L));
    }

    @Test
    void getAll_ShouldReturnImmutableCopy() {
        Trainee trainee = buildTrainee(1L);
        traineeStorage.getTrainees().put(1L, trainee);

        Map<Long, Trainee> result = dao.getAll();

        assertThrows(UnsupportedOperationException.class, () -> {
            result.put(2L, buildTrainee(2L));
        });
    }

    @Test
    void update_NonExistingTrainee_ShouldThrowException() {
        Trainee trainee = buildTrainee(999L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            dao.update(trainee);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

}