package com.epam.gymcrm.dao;

import com.epam.gymcrm.dao.interfaces.TrainingDao;
import com.epam.gymcrm.dao.searchCriteria.TraineeTrainingSearchCriteria;
import com.epam.gymcrm.dao.searchCriteria.TrainerTrainingSearchCriteria;
import com.epam.gymcrm.domain.Training;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class TrainingDaoImp implements TrainingDao {
    private static final Logger log = LoggerFactory.getLogger(TrainingDaoImp.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Training training) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(training);
    }

    @Override
    public List<Training> findTraineeTrainings(String traineeUsername, TraineeTrainingSearchCriteria criteria) {
        Session session = sessionFactory.getCurrentSession();
        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("traineeUsername is required");
        }
        if (criteria == null) criteria = new TraineeTrainingSearchCriteria();

        StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        hql.append("select tr ")
                .append("from Training tr ")
                .append("join tr.traineeId t ")
                .append("join t.user tu ")
                .append("join tr.trainerId r ")
                .append("join r.user ru ")
                .append("left join tr.type tt ")
                .append("where tu.username = :traineeUsername ");

        params.put("traineeUsername", traineeUsername);

        if (criteria.getFromDate() != null) {
            hql.append("and tr.date >= :fromDate ");
            params.put("fromDate", toLocalDate(criteria.getFromDate()));
        }
        if (criteria.getToDate() != null) {
            hql.append("and tr.date <= :toDate ");
            params.put("toDate", toLocalDate(criteria.getToDate()));
        }

        if (criteria.getTrainerName() != null && !criteria.getTrainerName().isBlank()) {
            hql.append("and (")
                    .append(" lower(ru.firstName) like :trainerName ")
                    .append(" or lower(ru.lastName) like :trainerName ")
                    .append(" or lower(concat(ru.firstName, ' ', ru.lastName)) like :trainerName ")
                    .append(") ");
            params.put("trainerName", "%" + criteria.getTrainerName().toLowerCase().trim() + "%");
        }

        if (criteria.getTrainingType() != null && !criteria.getTrainingType().isBlank()) {
            hql.append("and lower(tt.trainingTypeName) = :trainingType ");
            params.put("trainingType", criteria.getTrainingType().toLowerCase().trim());
        }

        hql.append("order by tr.date desc");

        Query<Training> query = session.createQuery(hql.toString(), Training.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

    @Override
    public List<Training> findTrainerTrainings(String trainerUsername, TrainerTrainingSearchCriteria criteria) {
        Session session = sessionFactory.getCurrentSession();
        if (trainerUsername == null || trainerUsername.isBlank()) {
            throw new IllegalArgumentException("trainerUsername is required");
        }
        if (criteria == null) criteria = new TrainerTrainingSearchCriteria();

        StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        hql.append("select tr ")
                .append("from Training tr ")
                .append("join tr.trainerId r ")
                .append("join r.user ru ")
                .append("join tr.traineeId t ")
                .append("join t.user tu ")
                .append("left join tr.type tt ")
                .append("where ru.username = :trainerUsername ");

        params.put("trainerUsername", trainerUsername);

        if (criteria.getFromDate() != null) {
            hql.append("and tr.date >= :fromDate ");
            params.put("fromDate", toLocalDate(criteria.getFromDate()));
        }
        if (criteria.getToDate() != null) {
            hql.append("and tr.date <= :toDate ");
            params.put("toDate", toLocalDate(criteria.getToDate()));
        }

        if (criteria.getTraineeName() != null && !criteria.getTraineeName().isBlank()) {
            hql.append("and (")
                    .append(" lower(tu.firstName) like :traineeName ")
                    .append(" or lower(tu.lastName) like :traineeName ")
                    .append(" or lower(concat(tu.firstName, ' ', tu.lastName)) like :traineeName ")
                    .append(") ");
            params.put("traineeName", "%" + criteria.getTraineeName().toLowerCase().trim() + "%");
        }

        hql.append("order by tr.date desc");

        Query<Training> query = session.createQuery(hql.toString(), Training.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

    @Override
    public int deleteTrainingsByTraineeUsername(String traineeUsername) {
        Session session = sessionFactory.getCurrentSession();
        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("traineeUsername is required");
        }

        String hql = ""
                + "delete from Training tr "
                + "where tr.traineeId.user.username = :traineeUsername";

        return session.createQuery(hql)
                .setParameter("traineeUsername", traineeUsername)
                .executeUpdate();
    }

    private LocalDate toLocalDate(java.util.Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
