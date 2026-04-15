package com.epam.gymcrm.health;

import com.epam.gymcrm.repository.TrainingTypeRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeSeedHealthIndicator implements HealthIndicator {

    private static final long MIN_EXPECTED_TYPES = 4;

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeSeedHealthIndicator(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public Health health() {
        try {
            long actualCount = trainingTypeRepository.count();
            if (actualCount >= MIN_EXPECTED_TYPES) {
                return Health.up()
                        .withDetail("description", "Checks whether training types were seeded from SQL initialization")
                        .withDetail("endpoint", "/actuator/health/trainingTypeSeed")
                        .withDetail("statusPolicy", "UP when actualCount >= minExpected; DOWN otherwise")
                        .withDetail("actualCount", actualCount)
                        .withDetail("minExpected", MIN_EXPECTED_TYPES)
                        .build();
            }

            return Health.down()
                    .withDetail("description", "Checks whether training types were seeded from SQL initialization")
                    .withDetail("endpoint", "/actuator/health/trainingTypeSeed")
                    .withDetail("statusPolicy", "UP when actualCount >= minExpected; DOWN otherwise")
                    .withDetail("actualCount", actualCount)
                    .withDetail("minExpected", MIN_EXPECTED_TYPES)
                    .withDetail("reason", "Training types are not fully seeded")
                    .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}

