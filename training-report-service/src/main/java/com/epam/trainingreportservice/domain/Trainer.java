package com.epam.trainingreportservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "trainers")
@CompoundIndex(name = "trainer_name_idx", def = "{'firstName': 1, 'lastName': 1}")
public class Trainer {

    @Id
    private String id;

    @Indexed(unique = true)
    private String trainerUsername;

    @Field("firstName")
    private String firstName;

    @Field("lastName")
    private String lastName;

    @Field("status")
    private Boolean status;

    private List<TrainerYear> years;

    public Trainer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<TrainerYear> getYears() {
        return years;
    }

    public void setYears(List<TrainerYear> years) {
        this.years = years;
    }
}
