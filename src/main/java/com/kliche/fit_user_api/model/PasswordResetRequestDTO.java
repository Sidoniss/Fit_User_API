package com.kliche.fit_user_api.model;

import lombok.Data;

@Data
public class PasswordResetRequestDTO {
    private String token;
    private String newPassword;
}
