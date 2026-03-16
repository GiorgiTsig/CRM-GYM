package com.epam.gymcrm.dto.trainee;

import com.epam.gymcrm.dto.UserDto;

import java.time.LocalDate;
import java.util.List;

public class TraineeDto {
    private UserDto user;
    private LocalDate dateOfBirth;
    private String address;
    private List<TrainerDto> trainers;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

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

    public List<TrainerDto> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerDto> trainers) {
        this.trainers = trainers;
    }
}
