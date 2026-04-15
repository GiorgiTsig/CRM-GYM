package com.epam.gymcrm.dto.trainee.response;

import com.epam.gymcrm.dto.TrainingTypeDto;

import java.time.LocalDate;

public class TraineeTrainingDto {
    private String name;
    private LocalDate date;
    private TrainingTypeDto type;
    private Integer duration;
    private String trainerUsername;

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
}
