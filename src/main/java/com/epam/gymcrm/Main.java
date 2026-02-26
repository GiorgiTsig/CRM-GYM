package com.epam.gymcrm;

import com.epam.gymcrm.config.AppConfig;
import com.epam.gymcrm.domain.*;
import com.epam.gymcrm.facade.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.time.LocalDate;


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
        traineeFacade.createTraineeProfile(traineeUser, new Trainee(dateOfBirth, "f"), "Giorgi.Tsignadze");

        //Check the trainer’s username and password, and run it after that.
        trainingFacade.addTraining("Giorgi.Tsignadze", "Giorgi.Tsignadze1", new Training("Martial ART", localDate, 100));
    }
}
