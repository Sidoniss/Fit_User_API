package com.kliche.fit_user_api.controller;

import com.kliche.fit_user_api.model.*;
import com.kliche.fit_user_api.service.CustomUserDetailsService;
import com.kliche.fit_user_api.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private final PasswordResetService passwordResetService;

    public AuthController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<User> user = userDetailsService.authenticateUser(loginDto);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        LoginResponse loginResponse = userDetailsService.createLoginResponse(user.get());
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("signup")
    public ResponseEntity<LoginResponse> registerUser(@RequestBody SignUpDto signUpDto) {
        try {
            User user = userDetailsService.registerUser(signUpDto);
            LoginResponse loginResponse = userDetailsService.createLoginResponse(user);
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (Exception e) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setEmail(signUpDto.getEmail());
            return new ResponseEntity<>(loginResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody recoverDto recoverDto) {
        String email = recoverDto.getEmail();
        try {
            passwordResetService.generatePasswordResetToken(email);
            return ResponseEntity.ok("Token resetu hasła został wysłany na podany adres email.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequestDTO requestDTO) {
        String token = requestDTO.getToken();
        String newPassword = requestDTO.getNewPassword();
        try {
            passwordResetService.resetPassword(token,newPassword);
            return ResponseEntity.ok("Twoje hasło zostało zresetowane.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO request) {
        String email = request.getEmail();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        try {
            passwordResetService.changePassword(email,oldPassword,newPassword);
            return ResponseEntity.ok("Hasło zostało zmienione.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

