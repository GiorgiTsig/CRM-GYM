package com.epam.gymcrm.dao;

import com.epam.gymcrm.dao.interfaces.Dao;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TrainerDaoImp implements Dao<Trainer> {

    @Autowired
    private SessionFactory sessionFactory;

    private static final Logger log = LoggerFactory.getLogger(TrainerDaoImp.class);

    @Override
    public Optional<Trainer> get(String username) {
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

        TypedQuery<Trainer> trainerTypedQuery = session.createQuery("SELECT a from Trainer a WHERE a.user = :user_id", Trainer.class);
        Optional<Trainer> trainer = trainerTypedQuery
                .setParameter("user_id", user.get())
                .getResultList()
                .stream()
                .findFirst();

        if (trainer.isPresent()) {
            log.info("Trainer found with username {}", username);
        } else {
            log.info("Trainer not found with username {}", username);
        }
        return trainer;
    }

    @Override
    public List<Trainer> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT a FROM Trainer a", Trainer.class).getResultList();
    }

    @Override
    public void save(Trainer trainer) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(trainer);
    }

    @Override
    public void update(Trainer trainer) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(trainer);
    }

    @Override
    public void delete(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        Trainer trainer = session.get(Trainer.class, id);

        if (trainer == null) {
            log.error("Cannot delete. trainer with id {} not found", id);
            throw new EntityNotFoundException("trainer with id " + id + " not found");
        }

        session.remove(trainer.getUser());
    }
}
