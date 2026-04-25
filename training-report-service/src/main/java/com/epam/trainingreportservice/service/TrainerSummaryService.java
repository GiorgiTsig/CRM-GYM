package com.epam.trainingreportservice.service;

import com.epam.trainingreportservice.domain.TrainerMonth;
import com.epam.trainingreportservice.domain.TrainerYear;
import com.epam.trainingreportservice.dto.request.TrainingEventDto;
import com.epam.trainingreportservice.mapper.TrainerMapper;
import com.epam.trainingreportservice.domain.Trainer;
import com.epam.trainingreportservice.dto.request.ActionType;
import com.epam.trainingreportservice.dto.response.TrainerWorkloadResponse;
import com.epam.trainingreportservice.repository.TrainerRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Service
@Validated
public class TrainerSummaryService {

    private final TrainerRepository repository;
    private final TrainerMapper trainerMapper;

    public TrainerSummaryService(TrainerRepository repository,  TrainerMapper trainerMapper) {
        this.repository = repository;
        this.trainerMapper = trainerMapper;
    }

    @Transactional
    public void updateSummary(@Valid TrainingEventDto trainingEventDto) {
        Trainer trainer = repository.findByTrainerUsername(trainingEventDto.getTrainerUsername())
                .orElseGet(() -> createNewTrainer(trainingEventDto));


        TrainerYear summaryYear = getOrCreateTrainerYear(trainer, trainingEventDto.getTrainingDate().getYear());
        TrainerMonth summaryMonth = getOrCreateTrainerMonth(summaryYear, trainingEventDto.getTrainingDate().getMonthValue());

        if (ActionType.ADD == trainingEventDto.getAction()) {
            summaryMonth.setTrainingsSummaryDuration(summaryMonth.getTrainingsSummaryDuration() + trainingEventDto.getDuration());
        }

        if (ActionType.DELETE == trainingEventDto.getAction()) {
            summaryMonth.setTrainingsSummaryDuration(Math.max(0, summaryMonth.getTrainingsSummaryDuration() - trainingEventDto.getDuration()));
        }

        repository.save(trainer);
    }


    private Trainer createNewTrainer(TrainingEventDto trainingEventDto) {
        Trainer trainer = new Trainer();

        trainer.setTrainerUsername(trainingEventDto.getTrainerUsername());
        trainer.setFirstName(trainingEventDto.getFirstName());
        trainer.setLastName(trainingEventDto.getLastName());
        trainer.setStatus(trainingEventDto.isActive());
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
        trainerMonth.setTrainingsSummaryDuration(0);
        trainerYear.getMonths().add(trainerMonth);
        return trainerMonth;
    }

    @Transactional(readOnly = true)
    public TrainerWorkloadResponse getTrainerByUsername(String username) {
        return repository.findByTrainerUsername(username)
                .map(trainerMapper::toDto)
                .orElse(null);
    }
}
