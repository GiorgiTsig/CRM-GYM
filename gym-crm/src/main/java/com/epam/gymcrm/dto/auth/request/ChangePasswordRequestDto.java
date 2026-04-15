package com.epam.gymcrm.dto.auth.request;

public class ChangePasswordRequestDto {
    private String newPassword;


    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
