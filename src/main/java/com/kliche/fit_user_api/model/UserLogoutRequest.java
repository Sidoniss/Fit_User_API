package com.kliche.fit_user_api.model;

public class UserLogoutRequest {
    private String jwtToken;

    public UserLogoutRequest(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
