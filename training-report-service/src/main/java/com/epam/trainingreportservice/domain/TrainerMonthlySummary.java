package com.epam.trainingreportservice.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "trainer_summary")
public class TrainerMonthlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String trainerUsername;
    private String firstName;
    private String lastName;
    private Boolean active;

    @Column(name = "year_value")
    private int year;

    @Column(name = "month_value")
    private int monthValue;

    private int totalDuration;

    public TrainerMonthlySummary() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonthValue() {
        return monthValue;
    }

    public void setMonthValue(int monthValue) {
        this.monthValue = monthValue;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }
}
