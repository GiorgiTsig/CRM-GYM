package com.epam.trainingreportservice.dto.response;

public class MonthSummaryDto {
    private int month;

    private int trainingsSummaryDuration;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getTrainingsSummaryDuration() {
        return trainingsSummaryDuration;
    }

    public void setTrainingsSummaryDuration(int trainingsSummaryDuration) {
        this.trainingsSummaryDuration = trainingsSummaryDuration;
    }
}
