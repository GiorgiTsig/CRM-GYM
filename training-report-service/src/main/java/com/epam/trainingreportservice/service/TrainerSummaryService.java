package com.epam.trainingreportservice.service;

import com.epam.trainingreportservice.domain.TrainerMonth;
import com.epam.trainingreportservice.domain.TrainerYear;
import com.epam.trainingreportservice.mapper.TrainerMapper;
import com.epam.trainingreportservice.domain.Trainer;
import com.epam.trainingreportservice.dto.request.ActionType;
import com.epam.trainingreportservice.dto.response.TrainerWorkloadResponse;
import com.epam.trainingreportservice.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class TrainerSummaryService {

    private final TrainerRepository repository;
    private final TrainerMapper trainerMapper;

    public TrainerSummaryService(TrainerRepository repository,  TrainerMapper trainerMapper) {
        this.repository = repository;
        this.trainerMapper = trainerMapper;
    }

    @Transactional
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

        Trainer summary = Optional.ofNullable(repository.findByTrainerUsername(username))
                .orElseGet(() -> createNewTrainer(username, firstName, lastName, active));

        TrainerYear summaryYear = getOrCreateTrainerYear(summary, year);
        TrainerMonth summaryMonth = getOrCreateTrainerMonth(summaryYear, month);

        if (ActionType.ADD == action) {
            summaryMonth.setTotalDuration(summaryMonth.getTotalDuration() + duration);
        }

        if (ActionType.DELETE == action) {
            summaryMonth.setTotalDuration(Math.max(0, summaryMonth.getTotalDuration() - duration));
        }

        repository.save(summary);
    }


    private Trainer createNewTrainer(
            String username,
            String firstName,
            String lastName,
            boolean active
    ) {
        Trainer trainer = new Trainer();

        trainer.setTrainerUsername(username);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setActive(active);
        trainer.setYears(new ArrayList<>());
        return trainer;
    }

    private TrainerYear getOrCreateTrainerYear(Trainer trainer, int year) {
        return trainer.getYears().stream()
                .filter(existingYear -> existingYear.getYear() == year)
                .findFirst()
                .orElseGet(() -> createTrainerYear(trainer, year));
    }

    private TrainerYear createTrainerYear(Trainer trainer, int year) {
        TrainerYear trainerYear = new TrainerYear();
        trainerYear.setYear(year);
        trainerYear.setMonths(new ArrayList<>());
        trainerYear.setTrainer(trainer);
        trainer.getYears().add(trainerYear);
        return trainerYear;
    }

    private TrainerMonth getOrCreateTrainerMonth(TrainerYear trainerYear, int month) {
        return trainerYear.getMonths().stream()
                .filter(existingMonth -> existingMonth.getMonth() == month)
                .findFirst()
                .orElseGet(() -> createTrainerMonth(trainerYear, month));
    }

    private TrainerMonth createTrainerMonth(TrainerYear trainerYear, int month) {
        TrainerMonth trainerMonth = new TrainerMonth();
        trainerMonth.setMonth(month);
        trainerMonth.setTotalDuration(0);
        trainerMonth.setTrainerYear(trainerYear);
        trainerYear.getMonths().add(trainerMonth);
        return trainerMonth;
    }

    @Transactional(readOnly = true)
    public TrainerWorkloadResponse getTrainerByUsername(String username) {
        Trainer trainers =  repository.findByTrainerUsername(username);
        return trainerMapper.toDto(trainers);
    }
}
