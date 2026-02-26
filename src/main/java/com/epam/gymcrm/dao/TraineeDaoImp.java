package com.epam.gymcrm.dao;

import com.epam.gymcrm.dao.interfaces.Dao;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TraineeDaoImp implements Dao<Trainee> {

    @Autowired
    private SessionFactory sessionFactory;

    private static final Logger log = LoggerFactory.getLogger(TraineeDaoImp.class);

    @Override
    public Optional<Trainee> get(String username) {
        Session session = sessionFactory.getCurrentSession();

        TypedQuery<User> userTypedQuery = session.createQuery("SELECT a from User a WHERE  a.username = :username", User.class);
        Optional<User> user = userTypedQuery
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();

        if (user.isEmpty()) {
            log.info("User not found with username {}", username);
            return Optional.empty();
        }

        TypedQuery<Trainee> traineeTypedQuery = session.createQuery("SELECT a from Trainee a WHERE a.user = :user_id", Trainee.class);
        Optional<Trainee> trainee = traineeTypedQuery
                .setParameter("user_id", user.get())
                .getResultList()
                .stream()
                .findFirst();

        if (trainee.isEmpty()) {
            log.info("Trainee not found with username {}", username);
        }

        return trainee;
    }

    @Override
    public List<Trainee> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT a FROM Trainee a", Trainee.class).getResultList();
    }

    @Override
    public void save(Trainee trainee) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(trainee);
    }

    @Override
    public void update(Trainee trainee) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(trainee);
    }

    @Override
    public void delete(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        Trainee trainee = session.get(Trainee.class, id);

        if (trainee == null) {
            log.error("Cannot delete. trainer with id {} not found", id);
            throw new EntityNotFoundException("trainer with id " + id + " not found");
        }

        session.remove(trainee.getUser());
    }

    public void delete(String username) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<User> userTypedQuery = session.createQuery("SELECT a from User a WHERE  a.username = :username", User.class);
        User user = userTypedQuery.setParameter("username", username).getSingleResult();

        TypedQuery<Trainee> traineeTypedQuery = session.createQuery("SELECT t FROM Trainee t WHERE t.user.id = :userId", Trainee.class);
        Trainee trainee = traineeTypedQuery.setParameter("userId", user.getId()).getSingleResult();

        if (trainee == null) {
            log.error("Cannot delete. trainer with username {} not found", username);
            throw new EntityNotFoundException("trainer with username " + username + " not found");
        }

        for (Trainer tr : new HashSet<>(trainee.getTrainers())) {
            tr.getTrainees().remove(trainee);
            trainee.getTrainers().remove(tr);
        }

        session.remove(user);
    }

    public Optional<Trainee> getById(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        Trainee trainee = session.get(Trainee.class, id);
        return Optional.ofNullable(trainee);
    }
}
