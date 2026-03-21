package com.epam.gymcrm;

import com.epam.gymcrm.config.AppConfig;
import com.epam.gymcrm.domain.*;
import com.epam.gymcrm.facade.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.time.LocalDate;
import java.util.Set;


public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        TrainerFacade trainerFacade = applicationContext.getBean(TrainerFacade.class);
        TraineeFacade traineeFacade = applicationContext.getBean(TraineeFacade.class);
        TrainingFacade trainingFacade = applicationContext.getBean(TrainingFacade.class);

        User trainerUser = new User("Giorgi", "Tsignadze", true);
        User traineeUser = new User("Giorgi", "Tsignadze", true);
        Set<String> trainers = Set.of();

        LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);
        LocalDate localDate = LocalDate.of(2025, 1, 1);

        //trainingFacade.addTraining("Giorgi.Tsignadze", "Giorgi.Tsignadze1", new Training("Martial ART", localDate, 100));


        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2026, 3, 16);

        //Check the password in the database and set it; otherwise, it won’t work.
        //trainingFacade.getTrainerTrainings("Sarah.Williams", "aLPyO5Z5RA", trainerTrainingSearchCriteria);
        //traineeFacade.updateTraineeTrainers("Jane.Smith", "shlVbr6yWY", trainers);
    }
}
