package com.epam.trainingreportservice.service;

import com.epam.trainingreportservice.domain.Trainer;
import com.epam.trainingreportservice.domain.TrainerMonth;
import com.epam.trainingreportservice.domain.TrainerYear;
import com.epam.trainingreportservice.dto.request.ActionType;
import com.epam.trainingreportservice.dto.request.TrainingEventDto;
import com.epam.trainingreportservice.mapper.TrainerMapper;
import com.epam.trainingreportservice.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerSummaryServiceTest {

    @Mock
    private TrainerRepository repository;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerSummaryService trainerSummaryService;

    @Test
    void shouldCreateTrainerSummaryWhenTrainerDoesNotExist() {
        TrainingEventDto event = buildEvent();
        when(repository.findByTrainerUsername("trainer.user")).thenReturn(Optional.empty());

        trainerSummaryService.updateSummary(event);

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(repository).save(trainerCaptor.capture());

        Trainer savedTrainer = trainerCaptor.getValue();
        assertThat(savedTrainer.getTrainerUsername()).isEqualTo("trainer.user");
        assertThat(savedTrainer.getFirstName()).isEqualTo("John");
        assertThat(savedTrainer.getLastName()).isEqualTo("Doe");
        assertThat(savedTrainer.getStatus()).isTrue();
        assertThat(savedTrainer.getYears()).hasSize(1);
        assertThat(savedTrainer.getYears().get(0).getYear()).isEqualTo(2026);
        assertThat(savedTrainer.getYears().get(0).getMonths()).hasSize(1);
        assertThat(savedTrainer.getYears().get(0).getMonths().get(0).getMonth()).isEqualTo(4);
        assertThat(savedTrainer.getYears().get(0).getMonths().get(0).getTrainingsSummaryDuration()).isEqualTo(90);
        verify(trainerMapper, never()).toDto(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldAddDurationToExistingMonthSummary() {
        TrainingEventDto event = buildEvent();
        Trainer trainer = existingTrainer(2026, 4, 120);
        when(repository.findByTrainerUsername("trainer.user")).thenReturn(Optional.of(trainer));

        trainerSummaryService.updateSummary(event);

        verify(repository).save(trainer);
        assertThat(trainer.getYears()).hasSize(1);
        assertThat(trainer.getYears().get(0).getMonths()).hasSize(1);
        assertThat(trainer.getYears().get(0).getMonths().get(0).getTrainingsSummaryDuration()).isEqualTo(210);
    }

    @Test
    void shouldCreateMonthSummaryInsideExistingYear() {
        TrainingEventDto event = buildEvent();
        Trainer trainer = existingTrainer(2026, 3, 50);
        when(repository.findByTrainerUsername("trainer.user")).thenReturn(Optional.of(trainer));

        trainerSummaryService.updateSummary(event);

        verify(repository).save(trainer);
        assertThat(trainer.getYears()).hasSize(1);
        assertThat(trainer.getYears().get(0).getMonths()).hasSize(2);
        assertThat(trainer.getYears().get(0).getMonths())
                .anySatisfy(month -> {
                    assertThat(month.getMonth()).isEqualTo(4);
                    assertThat(month.getTrainingsSummaryDuration()).isEqualTo(90);
                });
    }

    private TrainingEventDto buildEvent() {
        TrainingEventDto event = new TrainingEventDto();
        event.setTrainerUsername("trainer.user");
        event.setFirstName("John");
        event.setLastName("Doe");
        event.setActive(true);
        event.setTrainingDate(LocalDate.of(2026, 4, 25));
        event.setDuration(90);
        event.setAction(ActionType.ADD);
        return event;
    }

    private Trainer existingTrainer(int year, int month, int duration) {
        Trainer trainer = new Trainer();
        trainer.setTrainerUsername("trainer.user");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setStatus(true);
        trainer.setYears(new ArrayList<>());

        TrainerYear trainerYear = new TrainerYear();
        trainerYear.setYear(year);
        trainerYear.setMonths(new ArrayList<>(List.of(existingMonth(month, duration))));
        trainer.getYears().add(trainerYear);
        return trainer;
    }

    private TrainerMonth existingMonth(int month, int duration) {
        TrainerMonth trainerMonth = new TrainerMonth();
        trainerMonth.setMonth(month);
        trainerMonth.setTrainingsSummaryDuration(duration);
        return trainerMonth;
    }
}
