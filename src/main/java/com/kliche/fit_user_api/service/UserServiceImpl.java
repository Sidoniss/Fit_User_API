package com.kliche.fit_user_api.service;

import com.kliche.fit_user_api.model.User;
import com.kliche.fit_user_api.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public UserServiceImpl(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerUser(String email, String password, String name) {
        System.out.println("Inside registerUser method in UserService");
        if(userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Użytkownik o podanej nazwie już istnieje");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);

        return userRepository.save(user);
    }

    @Override
    public void login(User user) {
        String email = user.getEmail();
        String password = user.getPassword();

        String jwtToken = authenticationService.authenticateUser(email,password);
    }

    @Override
    public void logout(User user) {

    }
}
