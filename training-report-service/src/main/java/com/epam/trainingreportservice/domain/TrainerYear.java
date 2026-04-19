package com.epam.trainingreportservice.domain;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "trainer_year",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_id", "year_value"})
)
public class TrainerYear {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "year_value")
    private int year;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @OneToMany(mappedBy = "trainerYear", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainerMonth> months;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public List<TrainerMonth> getMonths() {
        return months;
    }

    public void setMonths(List<TrainerMonth> months) {
        this.months = months;
    }
}
