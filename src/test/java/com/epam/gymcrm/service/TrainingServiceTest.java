package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TrainingDaoImp;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.storage.TrainingStorage;
import com.epam.gymcrm.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainingServiceTest {

    private TrainingService trainingService;
    private TrainingDaoImp trainingDao;
    private TrainingStorage trainingStorage;
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        trainingStorage = new TrainingStorage();
        
        trainingDao = new TrainingDaoImp();
        trainingDao.setTraining(trainingStorage);
        
        idGenerator = new IdGenerator();
        
        trainingService = new TrainingService();
        trainingService.setTrainingDao(trainingDao);
        trainingService.setIdGenerator(idGenerator);
    }

    private Trainee createTrainee(Long id) {
        User user = new User(id, "John", "Doe", "john.doe", "password123", true);
        return new Trainee("01 January 1990", "123 Main St", user);
    }

    private Trainer createTrainer(Long id) {
        User user = new User(id, "Jane", "Smith", "jane.smith", "password456", true);
        return new Trainer(user, "Yoga");
    }

    @Test
    void createTraining() {
        Trainee trainee = createTrainee(1L);
        Trainer trainer = createTrainer(2L);
        String name = "Morning Yoga Session";
        TrainingType type = TrainingType.Yoga;
        String date = "2024-01-15";
        String duration = "60 minutes";

        Training createdTraining = trainingService.createTraining(trainee, trainer, name, type, date, duration);

        assertNotNull(createdTraining);
        assertEquals(trainee, createdTraining.getTraineeId());
        assertEquals(trainer, createdTraining.getTrainerId());
        assertEquals(name, createdTraining.getName());
        assertEquals(type, createdTraining.getType());
        assertEquals(date, createdTraining.getDate());
        assertEquals(duration, createdTraining.getDuration());
        assertNotNull(createdTraining.getId());
        
        assertTrue(trainingStorage.getTraining().containsKey(createdTraining.getId()));
        assertEquals(createdTraining, trainingStorage.getTraining().get(createdTraining.getId()));
    }

    @Test
    void createTraining_ShouldGenerateUniqueId() {
        Trainee trainee1 = createTrainee(1L);
        Trainer trainer1 = createTrainer(2L);
        
        Trainee trainee2 = createTrainee(3L);
        Trainer trainer2 = createTrainer(4L);

        Training training1 = trainingService.createTraining(trainee1, trainer1, "Session 1", TrainingType.Yoga, "2024-01-15", "60");
        Training training2 = trainingService.createTraining(trainee2, trainer2, "Session 2", TrainingType.MMA, "2024-01-16", "90");

        assertNotEquals(training1.getId(), training2.getId());
        assertTrue(training1.getId() < training2.getId());
    }

    @Test
    void createTraining_WithDifferentTypes() {
        Trainee trainee = createTrainee(1L);
        Trainer trainer = createTrainer(2L);

        Training yogaTraining = trainingService.createTraining(trainee, trainer, "Yoga Class", TrainingType.Yoga, "2024-01-15", "60");
        Training mmaTraining = trainingService.createTraining(trainee, trainer, "MMA Class", TrainingType.MMA, "2024-01-16", "90");
        Training boxTraining = trainingService.createTraining(trainee, trainer, "Box Class", TrainingType.Box, "2024-01-17", "45");
        Training pilatesTraining = trainingService.createTraining(trainee, trainer, "Pilates Class", TrainingType.Pilates, "2024-01-18", "30");

        assertEquals(TrainingType.Yoga, yogaTraining.getType());
        assertEquals(TrainingType.MMA, mmaTraining.getType());
        assertEquals(TrainingType.Box, boxTraining.getType());
        assertEquals(TrainingType.Pilates, pilatesTraining.getType());
    }

    @Test
    void selectTraining_ExistingId_ShouldReturnTraining() {
        Trainee trainee = createTrainee(1L);
        Trainer trainer = createTrainer(2L);
        Training training = trainingService.createTraining(trainee, trainer, "Test Session", TrainingType.Yoga, "2024-01-15", "60");
        
        Optional<Training> result = trainingService.selectTraining(training.getId());
        
        assertTrue(result.isPresent());
        assertEquals(training.getId(), result.get().getId());
        assertEquals("Test Session", result.get().getName());
    }

    @Test
    void selectTraining_NonExistingId_ShouldReturnEmpty() {
        Optional<Training> result = trainingService.selectTraining(999L);
        
        assertFalse(result.isPresent());
    }

    @Test
    void selectAllTrainings_ShouldReturnAllTrainings() {
        Trainee trainee1 = createTrainee(1L);
        Trainer trainer1 = createTrainer(2L);
        Trainee trainee2 = createTrainee(3L);
        Trainer trainer2 = createTrainer(4L);
        
        trainingService.createTraining(trainee1, trainer1, "Session 1", TrainingType.Yoga, "2024-01-15", "60");
        trainingService.createTraining(trainee2, trainer2, "Session 2", TrainingType.MMA, "2024-01-16", "90");
        trainingService.createTraining(trainee1, trainer2, "Session 3", TrainingType.Box, "2024-01-17", "45");
        
        var allTrainings = trainingService.selectAllTrainings();
        
        assertEquals(3, allTrainings.size());
    }

    @Test
    void createTraining_WithDifferentDurations() {
        Trainee trainee = createTrainee(1L);
        Trainer trainer = createTrainer(2L);

        Training shortSession = trainingService.createTraining(trainee, trainer, "Short Session", TrainingType.Yoga, "2024-01-15", "30 minutes");
        Training mediumSession = trainingService.createTraining(trainee, trainer, "Medium Session", TrainingType.MMA, "2024-01-16", "60 minutes");
        Training longSession = trainingService.createTraining(trainee, trainer, "Long Session", TrainingType.Pilates, "2024-01-17", "120 minutes");

        assertEquals("30 minutes", shortSession.getDuration());
        assertEquals("60 minutes", mediumSession.getDuration());
        assertEquals("120 minutes", longSession.getDuration());
    }}