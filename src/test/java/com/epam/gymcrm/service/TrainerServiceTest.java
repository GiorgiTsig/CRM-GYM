package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TraineeDaoImp;
import com.epam.gymcrm.dao.TrainerDaoImp;
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

class TrainerServiceTest {

    private TrainerService trainerService;
    private TrainerDaoImp trainerDao;
    private TraineeDaoImp traineeDao;
    private TrainersStorage trainersStorage;
    private TraineeStorage traineeStorage;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        trainersStorage = new TrainersStorage();
        traineeStorage = new TraineeStorage();
        
        trainerDao = new TrainerDaoImp();
        trainerDao.setTrainersStorage(trainersStorage);
        
        traineeDao = new TraineeDaoImp();
        traineeDao.setTraineeStorageStorage(traineeStorage);
        
        usernameGenerator = new UsernameGenerator();
        usernameGenerator.setTraineeDao(traineeDao);
        usernameGenerator.setTrainerDao(trainerDao);
        
        passwordGenerator = new PasswordGenerator();
        idGenerator = new IdGenerator();
        
        trainerService = new TrainerService();
        trainerService.setTrainerDao(trainerDao);
        trainerService.setUsernameGenerator(usernameGenerator);
        trainerService.setPasswordGenerator(passwordGenerator);
        trainerService.setIdGenerator(idGenerator);
    }

    @Test
    void createTrainer() {
        String firstName = "John";
        String lastName = "Smith";
        String specialization = "Yoga";

        Trainer createdTrainer = trainerService.createTrainer(firstName, lastName, specialization);

        assertNotNull(createdTrainer);
        assertEquals(firstName, createdTrainer.getFirstName());
        assertEquals(lastName, createdTrainer.getLastName());
        assertEquals(specialization, createdTrainer.getSpecialization());
        assertTrue(createdTrainer.isActive());
        assertNotNull(createdTrainer.getUsername());
        assertNotNull(createdTrainer.getPassword());
        assertEquals(10, createdTrainer.getPassword().length());
        assertEquals(firstName + "." + lastName, createdTrainer.getUsername());
        
        assertTrue(trainersStorage.getTrainers().containsKey(createdTrainer.getId()));
        assertEquals(createdTrainer, trainersStorage.getTrainers().get(createdTrainer.getId()));
    }

    @Test
    void createTrainer_WithDuplicateName_ShouldAddSerialNumber() {
        String firstName = "John";
        String lastName = "Doe";
        String specialization = "MMA";

        Trainer firstTrainer = trainerService.createTrainer(firstName, lastName, specialization);
        assertEquals(firstName + "." + lastName, firstTrainer.getUsername());

        Trainer secondTrainer = trainerService.createTrainer(firstName, lastName, specialization);
        assertEquals(firstName + "." + lastName + "1", secondTrainer.getUsername());

        Trainer thirdTrainer = trainerService.createTrainer(firstName, lastName, specialization);
        assertEquals(firstName + "." + lastName + "2", thirdTrainer.getUsername());
    }

    @Test
    void createTrainer_ShouldGenerateUniqueId() {
        Trainer trainer1 = trainerService.createTrainer("Alice", "Brown", "Pilates");
        Trainer trainer2 = trainerService.createTrainer("Bob", "White", "Box");

        assertNotEquals(trainer1.getId(), trainer2.getId());
        assertTrue(trainer1.getId() < trainer2.getId());
    }

    @Test
    void createTrainer_ShouldGeneratePassword() {
        Trainer trainer = trainerService.createTrainer("Test", "User", "Yoga");

        assertNotNull(trainer.getPassword());
        assertEquals(10, trainer.getPassword().length());
        assertTrue(trainer.getPassword().matches("[A-Za-z0-9]{10}"));
    }

    @Test
    void createTrainer_WithExistingTraineeName_ShouldAddSerialNumber() {
        String firstName = "Mike";
        String lastName = "Johnson";
        
        TraineeService traineeService = new TraineeService();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setUsernameGenerator(usernameGenerator);
        traineeService.setPasswordGenerator(passwordGenerator);
        traineeService.setIdGenerator(idGenerator);
        traineeService.createTrainee(firstName, lastName, "01 January 1990", "123 Main St");
        
        Trainer trainer = trainerService.createTrainer(firstName, lastName, "Yoga");
        assertEquals(firstName + "." + lastName + "1", trainer.getUsername());
    }

    @Test
    void selectTrainer_ExistingId_ShouldReturnTrainer() {
        Trainer trainer = trainerService.createTrainer("Tom", "Brown", "Pilates");
        
        Optional<Trainer> result = trainerService.selectTrainer(trainer.getId());
        
        assertTrue(result.isPresent());
        assertEquals(trainer.getId(), result.get().getId());
        assertEquals("Tom", result.get().getFirstName());
        assertEquals("Brown", result.get().getLastName());
        assertEquals("Pilates", result.get().getSpecialization());
    }

    @Test
    void selectTrainer_NonExistingId_ShouldReturnEmpty() {
        Optional<Trainer> result = trainerService.selectTrainer(999L);
        
        assertFalse(result.isPresent());
    }

    @Test
    void selectAllTrainers_ShouldReturnAllTrainers() {
        trainerService.createTrainer("Alice", "Smith", "Yoga");
        trainerService.createTrainer("Bob", "Johnson", "MMA");
        trainerService.createTrainer("Charlie", "Williams", "Box");
        
        var allTrainers = trainerService.selectAllTrainers();
        
        assertEquals(3, allTrainers.size());
    }

    @Test
    void updateTrainer_ExistingTrainer_ShouldUpdateSuccessfully() {
        Trainer original = trainerService.createTrainer("David", "Clark", "Yoga");
        
        Trainer updated = trainerService.updateTrainer(
                original.getId(),
                "David",
                "Clark",
                "MMA",
                false
        );
        
        assertEquals("MMA", updated.getSpecialization());
        assertFalse(updated.isActive());
        assertEquals(original.getUsername(), updated.getUsername());
        assertEquals(original.getPassword(), updated.getPassword());
    }

    @Test
    void updateTrainer_NonExistingTrainer_ShouldThrowException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            trainerService.updateTrainer(999L, "John", "Doe", "Yoga", true);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }
}
