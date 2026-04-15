package com.epam.gymcrm.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@Profile("stg")
public class StgInitializationFileHealthIndicator implements HealthIndicator {

    private final Resource initializationFile;
    private final String configuredLocation;

    public StgInitializationFileHealthIndicator(@Value("${storage.initialization.file.path}") Resource initializationFile,
                                                @Value("${storage.initialization.file.path}") String configuredLocation) {
        this.initializationFile = initializationFile;
        this.configuredLocation = configuredLocation;
    }

    @Override
    public Health health() {
        try {
            if (!initializationFile.exists()) {
                return Health.down()
                        .withDetail("description", "Checks stg initialization file availability and readability")
                        .withDetail("endpoint", "/actuator/health/stgInitializationFile")
                        .withDetail("statusPolicy", "UP when file exists and is readable; DOWN otherwise")
                        .withDetail("path", configuredLocation)
                        .withDetail("reason", "Initialization file is missing")
                        .build();
            }

            int firstByte;
            try (InputStream inputStream = initializationFile.getInputStream()) {
                firstByte = inputStream.read();
            }

            return Health.up()
                    .withDetail("description", "Checks stg initialization file availability and readability")
                    .withDetail("endpoint", "/actuator/health/stgInitializationFile")
                    .withDetail("statusPolicy", "UP when file exists and is readable; DOWN otherwise")
                    .withDetail("path", configuredLocation)
                    .withDetail("filename", initializationFile.getFilename())
                    .withDetail("empty", firstByte == -1)
                    .build();
        } catch (IOException e) {
            return Health.down(e)
                    .withDetail("description", "Checks stg initialization file availability and readability")
                    .withDetail("endpoint", "/actuator/health/stgInitializationFile")
                    .withDetail("statusPolicy", "UP when file exists and is readable; DOWN otherwise")
                    .withDetail("path", configuredLocation)
                    .build();
        }
    }
}

