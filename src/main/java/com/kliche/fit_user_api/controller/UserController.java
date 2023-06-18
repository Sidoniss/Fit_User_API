package com.kliche.fit_user_api.controller;

import com.kliche.fit_user_api.model.User;
import com.kliche.fit_user_api.model.UserLoginRequest;
import com.kliche.fit_user_api.model.UserLogoutRequest;
import com.kliche.fit_user_api.model.UserRegistrationRequest;
import com.kliche.fit_user_api.service.JwtUtils;
import com.kliche.fit_user_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        System.out.println("Inside registerUser method");
        String email = userRegistrationRequest.getEmail();
        String password = userRegistrationRequest.getPassword();
        String name = userRegistrationRequest.getName();
        System.out.println("Received data from request: " + userRegistrationRequest);
        System.out.println(email + " " + password + " " + name);

        try {
            User user = userService.registerUser(email,password,name);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        String email = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nieprawidłowe dane uwierzytelniające.");
        }

        userService.login(user);

        return ResponseEntity.ok("Zalogowano użytkownika: " + user.getEmail());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody UserLogoutRequest userLogoutRequest) {
        String jwtToken = userLogoutRequest.getJwtToken();

        if(!JwtUtils.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nieprawidłowy token JWT. Wylogowanie nieudane.");
        }

        JwtUtils.logoutUser(jwtToken);

        return ResponseEntity.ok("Wylogowano użytkownika.");
    }
}
