package com.kliche.fit_user_api.service;

import com.kliche.fit_user_api.model.*;
import com.kliche.fit_user_api.repository.RoleRepository;
import com.kliche.fit_user_api.repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.aspectj.apache.bcel.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;



    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow((() ->
                        new UsernameNotFoundException("User not found with email: " + email)));

        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName()));

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                authorities);
    }

    public Optional<User> authenticateUser(LoginDto loginDto) {
        return userRepository.findByEmail(loginDto.getEmail());
    }

    public LoginResponse createLoginResponse(User user) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setEmail(user.getEmail());
        loginResponse.setName(user.getName());
        loginResponse.setAccountCreationDate(user.getAccountCreationDate().toString());
        return loginResponse;
    }

    public User registerUser(SignUpDto signUpDto) throws Exception {
        if (signUpDto.getEmail() == null || !EmailValidator.getInstance().isValid(signUpDto.getEmail())) {
            throw new Exception("Invalid email format");
        }

        if (signUpDto.getPassword() == null || signUpDto.getPassword().length() < 5) {
            throw new Exception("Password must be at least 5 characters long");
        }

        if (signUpDto.getName() == null || signUpDto.getName().length() < 2) {
            throw new Exception("Name must be at least 2 characters long");
        }

        if(userRepository.existsByEmail(signUpDto.getEmail())){
            throw new Exception("Email already in use");
        }

        User user = new User();
        user.setEmail(signUpDto.getEmail());
        user.setName(signUpDto.getName());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setAccountCreationDate(LocalDate.now());

        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new Exception("Role not found"));
        user.setRole(role);

        return userRepository.save(user);
    }


}
