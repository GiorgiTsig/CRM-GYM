package com.epam.gymcrm.dao.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Dao<T> {
    Optional<T> get(String username);

    List<T> getAll();

    void save(T t);

    void update(T t);

    void delete(UUID id);
}
