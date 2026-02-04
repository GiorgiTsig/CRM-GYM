package com.epam.gymcrm.integration;

import com.epam.gymcrm.config.AppConfig;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.facade.GymFacade;
import com.epam.gymcrm.service.TraineeService;
import com.epam.gymcrm.service.TrainerService;
import com.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GymCRMIntegrationTest {

    private ApplicationContext applicationContext;
    private GymFacade gymFacade;
    private TrainerService trainerService;
    private TraineeService traineeService;
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        gymFacade = applicationContext.getBean(GymFacade.class);
        trainerService = applicationContext.getBean(TrainerService.class);
        traineeService = applicationContext.getBean(TraineeService.class);
        trainingService = applicationContext.getBean(TrainingService.class);
    }

    @Test
    void springContextLoads() {
        assertNotNull(applicationContext);
        assertNotNull(gymFacade);
        assertNotNull(trainerService);
        assertNotNull(traineeService);
        assertNotNull(trainingService);
    }

    @Test
    void createTrainer_EndToEndFlow() {
        String firstName = "Integration";
        String lastName = "Test";
        String specialization = "Yoga";

        Trainer createdTrainer = gymFacade.createTrainer(firstName, lastName, specialization);

        assertNotNull(createdTrainer);
        assertNotNull(createdTrainer.getId());
        assertEquals(firstName, createdTrainer.getFirstName());
        assertEquals(lastName, createdTrainer.getLastName());
        assertEquals(specialization, createdTrainer.getSpecialization());
        assertEquals("Integration.Test", createdTrainer.getUsername());
        assertNotNull(createdTrainer.getPassword());
        assertEquals(10, createdTrainer.getPassword().length());
        assertTrue(createdTrainer.isActive());

        Optional<Trainer> retrieved = trainerService.selectTrainer(createdTrainer.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(createdTrainer.getId(), retrieved.get().getId());
    }

    @Test
    void createTrainee_EndToEndFlow() {
        String firstName = "Test";
        String lastName = "User";
        String dateOfBirth = "1990-01-15";
        String address = "123 Test St";

        Trainee createdTrainee = gymFacade.createTrainee(firstName, lastName, dateOfBirth, address);

        assertNotNull(createdTrainee);
        assertNotNull(createdTrainee.getId());
        assertEquals(firstName, createdTrainee.getFirstName());
        assertEquals(lastName, createdTrainee.getLastName());
        assertEquals(dateOfBirth, createdTrainee.getDateOfBirth());
        assertEquals(address, createdTrainee.getAddress());
        assertEquals("Test.User", createdTrainee.getUsername());
        assertNotNull(createdTrainee.getPassword());
        assertEquals(10, createdTrainee.getPassword().length());

        Optional<Trainee> retrieved = traineeService.selectTrainee(createdTrainee.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(createdTrainee.getId(), retrieved.get().getId());
    }

    @Test
    void createTraining_EndToEndFlow() {
        Trainer trainer = gymFacade.createTrainer("Trainer", "One", "MMA");
        Trainee trainee = gymFacade.createTrainee("Trainee", "One", "1995-05-15", "456 Test Ave");

        String trainingName = "Integration Training";
        TrainingType type = TrainingType.MMA;
        String date = "2024-02-01";
        String duration = "90 minutes";

        Training createdTraining = gymFacade.createTraining(trainee, trainer, trainingName, type, date, duration);

        assertNotNull(createdTraining);
        assertNotNull(createdTraining.getId());
        assertEquals(trainingName, createdTraining.getName());
        assertEquals(type, createdTraining.getType());
        assertEquals(date, createdTraining.getDate());
        assertEquals(duration, createdTraining.getDuration());

        Optional<Training> retrieved = trainingService.selectTraining(createdTraining.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(createdTraining.getId(), retrieved.get().getId());
    }

    @Test
    void updateTrainee_EndToEndFlow() {
        Trainee trainee = traineeService.createTrainee("Original", "Name", "1990-01-01", "Original Address");
        Long traineeId = trainee.getId();

        Trainee updated = traineeService.updateTrainee(
                traineeId,
                "Updated",
                "Name",
                "1990-01-01",
                "Updated Address",
                false
        );

        assertEquals(traineeId, updated.getId());
        assertEquals("Updated", updated.getFirstName());
        assertEquals("Updated Address", updated.getAddress());
        assertFalse(updated.isActive());
        assertEquals(trainee.getUsername(), updated.getUsername());
    }

    @Test
    void updateTrainer_EndToEndFlow() {
        Trainer trainer = trainerService.createTrainer("Original", "Trainer", "Yoga");
        Long trainerId = trainer.getId();

        Trainer updated = trainerService.updateTrainer(
                trainerId,
                "Updated",
                "Trainer",
                "Pilates",
                false
        );

        assertEquals(trainerId, updated.getId());
        assertEquals("Updated", updated.getFirstName());
        assertEquals("Pilates", updated.getSpecialization());
        assertFalse(updated.isActive());
        assertEquals(trainer.getUsername(), updated.getUsername());
    }

    @Test
    void deleteTrainee_EndToEndFlow() {
        Trainee trainee = traineeService.createTrainee("ToDelete", "User", "1990-01-01", "Address");
        Long traineeId = trainee.getId();

        Optional<Trainee> beforeDelete = traineeService.selectTrainee(traineeId);
        assertTrue(beforeDelete.isPresent());

        traineeService.deleteTrainee(traineeId);

        Optional<Trainee> afterDelete = traineeService.selectTrainee(traineeId);
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void usernameCollisionHandling_EndToEndFlow() {
        String firstName = "UniqueFirstName";
        String lastName = "UniqueLastName";
        
        Trainer first = trainerService.createTrainer(firstName, lastName, "Yoga");
        assertEquals(firstName + "." + lastName, first.getUsername());

        Trainer second = trainerService.createTrainer(firstName, lastName, "MMA");
        assertEquals(firstName + "." + lastName + "1", second.getUsername());

        Trainer third = trainerService.createTrainer(firstName, lastName, "Pilates");
        assertEquals(firstName + "." + lastName + "2", third.getUsername());
    }

    @Test
    void crossEntityUsernameCollision_EndToEndFlow() {
        Trainer trainer = trainerService.createTrainer("Sam", "Smith", "Boxing");
        assertEquals("Sam.Smith", trainer.getUsername());

        Trainee trainee = traineeService.createTrainee("Sam", "Smith", "1995-01-01", "Address");
        assertEquals("Sam.Smith1", trainee.getUsername());
    }

    @Test
    void getAllTrainers_EndToEndFlow() {
        Map<Long, Trainer> initial = trainerService.selectAllTrainers();
        int initialSize = initial.size();

        trainerService.createTrainer("Trainer", "A", "Yoga");
        trainerService.createTrainer("Trainer", "B", "MMA");

        Map<Long, Trainer> after = trainerService.selectAllTrainers();
        assertEquals(initialSize + 2, after.size());
    }

    @Test
    void getAllTrainees_EndToEndFlow() {
        Map<Long, Trainee> initial = traineeService.selectAllTrainees();
        int initialSize = initial.size();

        traineeService.createTrainee("Trainee", "A", "1990-01-01", "Address A");
        traineeService.createTrainee("Trainee", "B", "1991-02-02", "Address B");

        Map<Long, Trainee> after = traineeService.selectAllTrainees();
        assertEquals(initialSize + 2, after.size());
    }
}
