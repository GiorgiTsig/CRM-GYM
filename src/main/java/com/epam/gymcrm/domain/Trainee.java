package com.epam.gymcrm.domain;

public class Trainee extends User {
    private String dateOfBirth;
    private String address;

    public Trainee(String dateOfBirth, String address, User user) {
        super(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getPassword(), user.isActive());
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Trainee{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", address='" + address + '\'' +
                ", isActive=" + isActive() +
                '}';
    }
}
