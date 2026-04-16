package com.epam.trainingreportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
public class TrainingReportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingReportServiceApplication.class, args);
    }

}
