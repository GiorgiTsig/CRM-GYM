package com.epam.gymcrm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Training {
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String name;
    private final TrainingType type;
    private String date;
    private String duration;

    public Training(Long id, Long traineeId, Long trainerId, String name, TrainingType type, String date, String duration) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.name = name;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }


    public Training(@JsonProperty("type") TrainingType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public TrainingType getType() {
        return type;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", traineeId=" + traineeId +
                ", trainerId=" + trainerId +
                ", name=" + name +
                ", type=" + type +
                ", date=" + date +
                ", duration=" + duration +
                '}';
    }
}
