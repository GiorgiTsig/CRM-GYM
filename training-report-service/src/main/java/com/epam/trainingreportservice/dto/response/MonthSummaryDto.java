package com.epam.trainingreportservice.dto.response;

public class MonthSummaryDto {
    private int month;

    private int totalDuration;

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
}
