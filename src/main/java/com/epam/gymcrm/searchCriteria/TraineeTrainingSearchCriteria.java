package com.epam.gymcrm.searchCriteria;

import java.time.LocalDate;

public class TraineeTrainingSearchCriteria {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainingType;

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }
}
