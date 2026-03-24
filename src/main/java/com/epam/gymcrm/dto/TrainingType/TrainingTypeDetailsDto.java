package com.epam.gymcrm.dto.TrainingType;

import java.util.UUID;

public class TrainingTypeDetailsDto {
    private UUID id;
    private String trainingTypeName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
