package com.epam.gymcrm.dao.searchCriteria;

import java.util.Date;

public class TrainerTrainingSearchCriteria {
    private Date fromDate;
    private Date toDate;
    private String traineeName;

    public Date getFromDate() { return fromDate; }
    public void setFromDate(Date fromDate) { this.fromDate = fromDate; }

    public Date getToDate() { return toDate; }
    public void setToDate(Date toDate) { this.toDate = toDate; }

    public String getTraineeName() { return traineeName; }
    public void setTraineeName(String traineeName) { this.traineeName = traineeName; }
}
