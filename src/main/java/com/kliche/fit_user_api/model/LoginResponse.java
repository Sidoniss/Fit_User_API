package com.kliche.fit_user_api.model;

import lombok.Data;

@Data
public class LoginResponse {
    private String email;
    private String name;
    private String AccountCreationDate;
}
