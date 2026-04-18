package com.epam.trainingreportservice.dto.response;

import java.util.List;

public class YearSummaryDto {
    private int year;

    private List<MonthSummaryDto> months;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<MonthSummaryDto> getMonths() {
        return months;
    }

    public void setMonths(List<MonthSummaryDto> months) {
        this.months = months;
    }
}
