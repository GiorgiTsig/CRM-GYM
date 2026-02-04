package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Trainee;

import java.util.Map;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> get(long id);

    Map<Long, T> getAll();

    void save(T t);

    void update(T t);

    void delete(long id);
}
