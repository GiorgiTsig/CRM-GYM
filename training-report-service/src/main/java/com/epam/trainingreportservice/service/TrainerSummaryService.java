package com.epam.trainingreportservice.service;

import com.epam.trainingreportservice.domain.TrainerMonthlySummary;
import com.epam.trainingreportservice.dto.ActionType;
import com.epam.trainingreportservice.repository.TrainerMonthlySummaryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TrainerSummaryService {

    private final TrainerMonthlySummaryRepository repository;

    public TrainerSummaryService(TrainerMonthlySummaryRepository repository) {
        this.repository = repository;
    }


    public void updateSummary(
            String username,
            String firstName,
            String lastName,
            boolean active,
            LocalDate date,
            int duration,
            ActionType action
    ) {

        int year = date.getYear();
        int month = date.getMonthValue();

        TrainerMonthlySummary summary = repository
                .findByTrainerUsernameAndYearAndMonthValue(username,  year, month)
                .orElseGet(() -> createNewSummary(username, firstName, lastName, active, year, month));

        if (ActionType.ADD == action) {
            summary.setTotalDuration(summary.getTotalDuration() + duration);
        }

        if (ActionType.DELETE == action) {
            summary.setTotalDuration(
                    Math.max(0, summary.getTotalDuration() - duration)
            );
        }

        repository.save(summary);
    }


    private TrainerMonthlySummary createNewSummary(
            String username,
            String firstName,
            String lastName,
            boolean active,
            int year,
            int month
    ) {
        TrainerMonthlySummary summary = new TrainerMonthlySummary();
        summary.setTrainerUsername(username);
        summary.setFirstName(firstName);
        summary.setLastName(lastName);
        summary.setActive(active);
        summary.setYear(year);
        summary.setMonthValue(month);
        summary.setTotalDuration(0);
        return summary;
    }

    public Optional<TrainerMonthlySummary> getTrainerSummary(String username, int year, int month) {
        return repository.findByTrainerUsernameAndYearAndMonthValue(username, year, month);
    }
}
