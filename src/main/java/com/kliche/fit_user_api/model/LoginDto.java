package com.kliche.fit_user_api.model;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
}
