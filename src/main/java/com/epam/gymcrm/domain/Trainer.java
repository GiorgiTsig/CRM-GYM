package com.epam.gymcrm.domain;

public class Trainer extends User {
    private String specialization;

    public Trainer(User user, String specialization) {
        super(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getPassword(), user.isActive());
        this.specialization = specialization;
    }

    public Trainer() {
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", isActive=" + isActive() +
                '}';
    }
}
