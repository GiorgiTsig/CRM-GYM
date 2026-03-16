package com.epam.gymcrm.dto.trainee;

import com.epam.gymcrm.dto.CreateUserDto;

import java.time.LocalDate;

public class CreateTraineeDto {
    private LocalDate dateOfBirth;
    private String address;
    private CreateUserDto user;

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CreateUserDto getUser() {
        return user;
    }

    public void setUser(CreateUserDto user) {
        this.user = user;
    }
}
