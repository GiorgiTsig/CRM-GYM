package com.epam.gymcrm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private Trainee traineeId;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainerId;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "trainingType_id")
    private TrainingType type;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer duration;

    public Training(UUID id, String name, TrainingType type, LocalDate date, Integer duration) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }

    public Training(String name, LocalDate date, Integer duration) {
        this.name = name;
        this.date = date;
        this.duration = duration;
    }


    public Training() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public TrainingType getType() {
        return type;
    }

    public Trainee getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Trainee traineeId) {
        this.traineeId = traineeId;
    }

    public Trainer getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Trainer trainerId) {
        this.trainerId = trainerId;
    }

    public void setType(TrainingType type) {
        this.type = type;
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
