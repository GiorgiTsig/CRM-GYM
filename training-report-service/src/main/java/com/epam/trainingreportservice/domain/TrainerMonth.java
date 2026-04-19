package com.epam.trainingreportservice.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "trainer_month",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_year_id", "month_value"})
)
public class TrainerMonth {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "month_value")
    private int month;

    private int totalDuration;

    @ManyToOne
    @JoinColumn(name = "trainer_year_id", nullable = false)
    private TrainerYear trainerYear;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public TrainerYear getTrainerYear() {
        return trainerYear;
    }

    public void setTrainerYear(TrainerYear trainerYear) {
        this.trainerYear = trainerYear;
    }
}
