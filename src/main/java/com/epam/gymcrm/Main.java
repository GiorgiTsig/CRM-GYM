package com.epam.gymcrm;

import com.epam.gymcrm.config.AppConfig;
import com.epam.gymcrm.searchCriteria.TrainerTrainingSearchCriteria;
import com.epam.gymcrm.domain.*;
import com.epam.gymcrm.facade.*;
import com.epam.gymcrm.service.TrainerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        TrainerFacade trainerFacade = applicationContext.getBean(TrainerFacade.class);
        TraineeFacade traineeFacade = applicationContext.getBean(TraineeFacade.class);
        TrainingFacade trainingFacade = applicationContext.getBean(TrainingFacade.class);

        User trainerUser = new User("Giorgi", "Tsignadze", true);
        User traineeUser = new User("Giorgi", "Tsignadze", true);

        LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);
        LocalDate localDate = LocalDate.of(2025, 1, 1);

        trainerFacade.createTrainerProfile(trainerUser, new Trainer(), "MMA");
        traineeFacade.createTraineeProfile(traineeUser, new Trainee(dateOfBirth, "f"));

        //trainingFacade.addTraining("Giorgi.Tsignadze", "Giorgi.Tsignadze1", new Training("Martial ART", localDate, 100));

        TrainerTrainingSearchCriteria trainerTrainingSearchCriteria = new TrainerTrainingSearchCriteria();

        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2026, 3, 16);

        trainerTrainingSearchCriteria.setFromDate(from);
        trainerTrainingSearchCriteria.setToDate(to);
        trainerTrainingSearchCriteria.setTraineeName("Jane.Smith");

        //Check the password in the database and set it; otherwise, it won’t work.
        //trainingFacade.getTrainerTrainings("Sarah.Williams", "aLPyO5Z5RA", trainerTrainingSearchCriteria);
        //traineeFacade.updateTraineeTrainers("Jane.Smith", "shlVbr6yWY", trainers, new ArrayList<>());

    }
}
