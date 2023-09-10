package com.kliche.fit_user_api.model;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String email;
    private String oldPassword;
    private String newPassword;
}
