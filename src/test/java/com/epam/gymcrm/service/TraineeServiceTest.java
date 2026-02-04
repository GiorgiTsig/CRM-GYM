package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TraineeDaoImp;
import com.epam.gymcrm.dao.TrainerDaoImp;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.storage.TraineeStorage;
import com.epam.gymcrm.storage.TrainersStorage;
import com.epam.gymcrm.util.IdGenerator;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TraineeServiceTest {

    private TraineeService traineeService;
    private TraineeDaoImp traineeDao;
    private TrainerDaoImp trainerDao;
    private TraineeStorage traineeStorage;
    private TrainersStorage trainersStorage;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        traineeStorage = new TraineeStorage();
        trainersStorage = new TrainersStorage();
        
        traineeDao = new TraineeDaoImp();
        traineeDao.setTraineeStorageStorage(traineeStorage);
        
        trainerDao = new TrainerDaoImp();
        trainerDao.setTrainersStorage(trainersStorage);
        
        usernameGenerator = new UsernameGenerator();
        usernameGenerator.setTraineeDao(traineeDao);
        usernameGenerator.setTrainerDao(trainerDao);
        
        passwordGenerator = new PasswordGenerator();
        idGenerator = new IdGenerator();
        
        traineeService = new TraineeService();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setUsernameGenerator(usernameGenerator);
        traineeService.setPasswordGenerator(passwordGenerator);
        traineeService.setIdGenerator(idGenerator);
    }

    @Test
    void createTrainee() {
        String firstName = "Jane";
        String lastName = "Doe";
        String dateOfBirth = "01 January 1990";
        String address = "123 Main St";

        Trainee createdTrainee = traineeService.createTrainee(firstName, lastName, dateOfBirth, address);

        assertNotNull(createdTrainee);
        assertEquals(firstName, createdTrainee.getFirstName());
        assertEquals(lastName, createdTrainee.getLastName());
        assertEquals(dateOfBirth, createdTrainee.getDateOfBirth());
        assertEquals(address, createdTrainee.getAddress());
        assertTrue(createdTrainee.isActive());
        assertNotNull(createdTrainee.getUsername());
        assertNotNull(createdTrainee.getPassword());
        assertEquals(10, createdTrainee.getPassword().length());
        assertEquals(firstName + "." + lastName, createdTrainee.getUsername());
        
        assertTrue(traineeStorage.getTrainees().containsKey(createdTrainee.getId()));
        assertEquals(createdTrainee, traineeStorage.getTrainees().get(createdTrainee.getId()));
    }

    @Test
    void createTrainee_WithDuplicateName_ShouldAddSerialNumber() {
        String firstName = "John";
        String lastName = "Smith";
        String dateOfBirth = "01 January 1990";
        String address = "123 Main St";

        Trainee firstTrainee = traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
        assertEquals(firstName + "." + lastName, firstTrainee.getUsername());

        Trainee secondTrainee = traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
        assertEquals(firstName + "." + lastName + "1", secondTrainee.getUsername());

        Trainee thirdTrainee = traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
        assertEquals(firstName + "." + lastName + "2", thirdTrainee.getUsername());
    }

    @Test
    void createTrainee_ShouldGenerateUniqueId() {
        Trainee trainee1 = traineeService.createTrainee("Alice", "Brown", "01 January 1990", "123 Main St");
        Trainee trainee2 = traineeService.createTrainee("Bob", "White", "02 February 1991", "456 Oak Ave");

        assertNotEquals(trainee1.getId(), trainee2.getId());
        assertTrue(trainee1.getId() < trainee2.getId());
    }

    @Test
    void createTrainee_ShouldGeneratePassword() {
        Trainee trainee = traineeService.createTrainee("Test", "User", "01 January 1990", "123 Main St");

        assertNotNull(trainee.getPassword());
        assertEquals(10, trainee.getPassword().length());
        assertTrue(trainee.getPassword().matches("[A-Za-z0-9]{10}"));
    }

    @Test
    void createTrainee_WithExistingTrainerName_ShouldAddSerialNumber() {
        String firstName = "Sarah";
        String lastName = "Williams";
        
        TrainerService trainerService = new TrainerService();
        trainerService.setTrainerDao(trainerDao);
        trainerService.setUsernameGenerator(usernameGenerator);
        trainerService.setPasswordGenerator(passwordGenerator);
        trainerService.setIdGenerator(idGenerator);
        trainerService.createTrainer(firstName, lastName, "Yoga");
        
        Trainee trainee = traineeService.createTrainee(firstName, lastName, "01 January 1990", "123 Main St");
        assertEquals(firstName + "." + lastName + "1", trainee.getUsername());
    }

    @Test
    void selectTrainee_ExistingId_ShouldReturnTrainee() {
        Trainee trainee = traineeService.createTrainee("Tom", "Brown", "15 March 1985", "456 Oak St");
        
        Optional<Trainee> result = traineeService.selectTrainee(trainee.getId());
        
        assertTrue(result.isPresent());
        assertEquals(trainee.getId(), result.get().getId());
        assertEquals("Tom", result.get().getFirstName());
        assertEquals("Brown", result.get().getLastName());
    }

    @Test
    void selectTrainee_NonExistingId_ShouldReturnEmpty() {
        Optional<Trainee> result = traineeService.selectTrainee(999L);
        
        assertFalse(result.isPresent());
    }

    @Test
    void selectAllTrainees_ShouldReturnAllTrainees() {
        traineeService.createTrainee("Alice", "Smith", "01 January 1990", "123 Main St");
        traineeService.createTrainee("Bob", "Johnson", "02 February 1991", "456 Oak Ave");
        traineeService.createTrainee("Charlie", "Williams", "03 March 1992", "789 Pine Rd");
        
        var allTrainees = traineeService.selectAllTrainees();
        
        assertEquals(3, allTrainees.size());
    }

    @Test
    void updateTrainee_ExistingTrainee_ShouldUpdateSuccessfully() {
        Trainee original = traineeService.createTrainee("David", "Clark", "01 January 1990", "123 Main St");
        
        Trainee updated = traineeService.updateTrainee(
                original.getId(),
                "David",
                "Clark",
                "02 February 1991",
                "789 New Address",
                false
        );
        
        assertEquals("02 February 1991", updated.getDateOfBirth());
        assertEquals("789 New Address", updated.getAddress());
        assertFalse(updated.isActive());
        assertEquals(original.getUsername(), updated.getUsername());
        assertEquals(original.getPassword(), updated.getPassword());
    }

    @Test
    void updateTrainee_NonExistingTrainee_ShouldThrowException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            traineeService.updateTrainee(999L, "John", "Doe", "01 January 1990", "123 Main St", true);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void deleteTrainee_ExistingTrainee_ShouldRemoveFromStorage() {
        Trainee trainee = traineeService.createTrainee("Eva", "Martinez", "01 January 1990", "123 Main St");
        Long traineeId = trainee.getId();
        
        assertTrue(traineeService.selectTrainee(traineeId).isPresent());
        
        traineeService.deleteTrainee(traineeId);
        
        assertFalse(traineeService.selectTrainee(traineeId).isPresent());
    }

    @Test
    void deleteTrainee_NonExistingTrainee_ShouldNotThrowException() {
        assertDoesNotThrow(() -> traineeService.deleteTrainee(999L));
    }
}
