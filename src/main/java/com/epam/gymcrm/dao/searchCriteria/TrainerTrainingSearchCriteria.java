package com.epam.gymcrm.dao.searchCriteria;

import java.time.LocalDate;

public class TrainerTrainingSearchCriteria {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeName;

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public String getTraineeName() { return traineeName; }
    public void setTraineeName(String traineeName) { this.traineeName = traineeName; }
}
