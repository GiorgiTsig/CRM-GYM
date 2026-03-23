package com.epam.gymcrm.dto.trainee.request;

import com.epam.gymcrm.dto.TrainingTypeDto;

import java.time.LocalDate;

public class TrainingRequestDto {
    private String authUsername;
    private String authPassword;
    private String name;
    private LocalDate date;
    private TrainingTypeDto type;
    private Integer duration;
    private String traineeUsername;
    private String trainerUsername;

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TrainingTypeDto getType() {
        return type;
    }

    public void setType(TrainingTypeDto type) {
        this.type = type;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }
}
