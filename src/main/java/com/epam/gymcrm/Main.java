package com.epam.gymcrm;

import com.epam.gymcrm.config.AppConfig;
import com.epam.gymcrm.facade.GymFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacade facade = applicationContext.getBean(GymFacade.class);
        facade.createTrainer("Giorgi", "Tsignadze", "martial art");

        System.out.println(facade.getAllTrainers());
    }
}
