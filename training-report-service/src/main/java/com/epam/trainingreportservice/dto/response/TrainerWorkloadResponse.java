package com.epam.trainingreportservice.dto.response;

import java.util.List;

public class TrainerWorkloadResponse {
    private String trainerUsername;
    private String firstName;
    private String lastName;
    private boolean active;
    private List<YearSummaryDto> years;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<YearSummaryDto> getYears() {
        return years;
    }

    public void setYears(List<YearSummaryDto> years) {
        this.years = years;
    }
}
