package com.kliche.fit_user_api.service;

import com.kliche.fit_user_api.model.PasswordResetToken;
import com.kliche.fit_user_api.model.User;
import com.kliche.fit_user_api.repository.PasswordResetTokenRepository;
import com.kliche.fit_user_api.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void generatePasswordResetToken(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Podany użytkownik nie istnieje!");
        }
        passwordResetTokenRepository.deleteByUser(user.get());

        String token = UUID.randomUUID().toString().substring(0,6);

        PasswordResetToken resetToken = new PasswordResetToken(user.get(),token);

        if (resetToken.getExpiration_date() == null) {
            resetToken.setExpiration_date(LocalDateTime.now().plusMinutes(30));
        }

        passwordResetTokenRepository.save(resetToken);

        String resetTokenMessage = "Twój token resetu hasła: " + token;
        emailService.sendPasswordResetEmail(user.get().getEmail(),resetTokenMessage);
    }

    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);
        if (resetTokenOptional.isEmpty() || resetTokenOptional.get().isExpired()) {
            throw new RuntimeException("Nieprawidłowy lub przestarzały token resetu hasła!");
        }

        PasswordResetToken resetToken = resetTokenOptional.get();
        User user = resetToken.getUser();
        user.setPassword(encodePassword(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    public void changePassword(String email,String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Podany użytkownik nie istnieje.");
        }

        User user = userOptional.get();
        String currentPassword = user.getPassword();

        if(!passwordEncoder.matches(oldPassword,currentPassword)) {
            throw new IllegalArgumentException("Niepoprawne stare hasło.");
        }

        String encodedNewPassword = encodePassword(newPassword);

        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    public String encodePassword(String newPassword) {
        return passwordEncoder.encode(newPassword);
    }
}
