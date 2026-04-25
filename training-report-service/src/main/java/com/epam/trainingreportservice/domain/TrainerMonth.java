package com.epam.trainingreportservice.domain;

public class TrainerMonth {
    private int month;

    private Integer trainingsSummaryDuration;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Integer getTrainingsSummaryDuration() {
        return trainingsSummaryDuration;
    }

    public void setTrainingsSummaryDuration(Integer trainingsSummaryDuration) {
        this.trainingsSummaryDuration = trainingsSummaryDuration;
    }
}
