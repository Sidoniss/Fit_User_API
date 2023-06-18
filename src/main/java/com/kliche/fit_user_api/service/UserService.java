package com.kliche.fit_user_api.service;

import com.kliche.fit_user_api.model.User;

public interface UserService {


    User findByEmail(String email);

    User registerUser(String email, String password, String name);

    void login(User user);

    void logout(User user);
}
