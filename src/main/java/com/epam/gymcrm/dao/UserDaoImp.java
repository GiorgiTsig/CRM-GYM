package com.epam.gymcrm.dao;

import com.epam.gymcrm.dao.interfaces.Dao;
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
public class UserDaoImp implements Dao<User> {

    @Autowired
    private SessionFactory sessionFactory;

    private static final Logger log = LoggerFactory.getLogger(UserDaoImp.class);

    @Override
    public Optional<User> get(String username) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<User> usernameQuery = session.createQuery("SELECT a FROM User a WHERE a.username = :username", User.class);
        return usernameQuery.setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<User> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT a FROM User a", User.class).getResultList();
    }

    @Override
    public void save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(user);
    }

    @Override
    public void update(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(user);
    }

    @Override
    public void delete(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, id);
        if (user == null) {
            log.error("Cannot delete. user with id {} not found", id);
            throw new EntityNotFoundException("user with id " + id + " not found");
        }
        session.remove(user);
    }
}
