package com.epam.gymcrm.storage;

import com.epam.gymcrm.domain.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingStorage {
    private final Map<Long, Training> training = new HashMap<>();

    public Map<Long, Training> getTraining() {
        return training;
    }
}
