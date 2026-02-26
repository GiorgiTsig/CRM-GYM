package com.epam.gymcrm.dao;

import com.epam.gymcrm.dao.interfaces.Dao;
import com.epam.gymcrm.domain.TrainingType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TrainingTypeDao implements Dao<TrainingType> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Optional<TrainingType> get(String username) {
        return Optional.empty();
    }

    @Override
    public List<TrainingType> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT a FROM TrainingType a", TrainingType.class).getResultList();
    }

    @Override
    public void save(TrainingType trainingType) {

    }

    @Override
    public void update(TrainingType trainingType) {

    }

    @Override
    public void delete(UUID id) {

    }
}
