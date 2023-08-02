package com.kliche.fit_user_api.controller;

import com.kliche.fit_user_api.model.*;
import com.kliche.fit_user_api.repository.RoleRepository;
import com.kliche.fit_user_api.repository.UserRepository;
import com.kliche.fit_user_api.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final PasswordResetService passwordResetService;

    public AuthController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setEmail(loginDto.getEmail());
        return new ResponseEntity<>(loginResponse,HttpStatus.OK);
    }

    @PostMapping("signup")
    public ResponseEntity<LoginResponse> registerUser(@RequestBody SignUpDto signUpDto) {
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setEmail(signUpDto.getEmail());
            return new ResponseEntity<>(loginResponse,HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(signUpDto.getEmail());
        user.setName(signUpDto.getName());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        Role roles = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setEmail(signUpDto.getEmail());

        return new ResponseEntity<>(loginResponse,HttpStatus.OK);
    }

    @PostMapping("request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody recoverDto recoverDto) {
        String email = recoverDto.getEmail();
        passwordResetService.generatePasswordResetToken(email);
        return ResponseEntity.ok("Token resetu hasła został wysłany na podany adres email.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequestDTO requestDTO) {
        String token = requestDTO.getToken();
        String newPassword = requestDTO.getNewPassword();
        passwordResetService.resetPassword(token,newPassword);
        return ResponseEntity.ok("Twoje hasło zostało zresetowane.");
    }
}








/*public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/regitration")
    public




//    @PostMapping("/registration")
//    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
//        System.out.println("Inside registerUser method");
//        String email = userRegistrationRequest.getEmail();
//        String password = userRegistrationRequest.getPassword();
//        String name = userRegistrationRequest.getName();
//        System.out.println("Received data from request: " + userRegistrationRequest);
//        System.out.println(email + " " + password + " " + name);
//
//        try {
//            User user = userService.registerUser(email,password,name);
//            return ResponseEntity.ok(user);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

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
}*/
