package com.epam.gymcrm.storage;

import com.epam.gymcrm.domain.Trainee;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TraineeStorage {
    private final Map<Long, Trainee> trainee = new HashMap<>();

    public Map<Long, Trainee> getTrainees() {
        return trainee;
    }
}
