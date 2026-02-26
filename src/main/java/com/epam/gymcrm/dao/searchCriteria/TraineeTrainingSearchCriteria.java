package com.epam.gymcrm.dao.searchCriteria;

import java.util.Date;

public class TraineeTrainingSearchCriteria {
    private Date fromDate;
    private Date toDate;
    private String trainerName;
    private String trainingType;

    public Date getFromDate() { return fromDate; }
    public void setFromDate(Date fromDate) { this.fromDate = fromDate; }

    public Date getToDate() { return toDate; }
    public void setToDate(Date toDate) { this.toDate = toDate; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }
}
