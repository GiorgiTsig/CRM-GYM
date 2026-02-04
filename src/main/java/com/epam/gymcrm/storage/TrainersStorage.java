package com.epam.gymcrm.storage;

import com.epam.gymcrm.domain.Trainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainersStorage {
    private final Map<Long, Trainer> trainer = new HashMap<>();

    public Map<Long, Trainer> getTrainers() {
        return trainer;
    }
}
