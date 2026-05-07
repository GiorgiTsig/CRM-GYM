package com.epam.trainingreportservice.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingEventDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldRejectMissingRequiredFields() {
        TrainingEventDto event = new TrainingEventDto();
        event.setTrainerUsername(" ");
        event.setFirstName(" ");
        event.setLastName(" ");
        event.setDuration(null);
        event.setAction(null);

        Set<ConstraintViolation<TrainingEventDto>> violations = validator.validate(event);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("trainerUsername", "firstName", "lastName", "trainingDate", "duration", "action");
    }

    @Test
    void shouldRejectNonPositiveDuration() {
        TrainingEventDto event = new TrainingEventDto();
        event.setTrainerUsername("trainer.user");
        event.setFirstName("John");
        event.setLastName("Doe");
        event.setTrainingDate(java.time.LocalDate.of(2026, 4, 25));
        event.setDuration(0);
        event.setAction(ActionType.ADD);

        Set<ConstraintViolation<TrainingEventDto>> violations = validator.validate(event);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("duration");
    }
}
