package com.epam.trainingreportservice.repository;

import com.epam.trainingreportservice.domain.TrainerMonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainerMonthlySummaryRepository extends JpaRepository<TrainerMonthlySummary, UUID> {

    Optional<TrainerMonthlySummary> findByTrainerUsernameAndYearAndMonthValue(String trainerUsername, int year, int monthValue);
}
