package com.kliche.fit_user_api;

import com.kliche.fit_user_api.repository.PasswordResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TokenCleanupJob {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public TokenCleanupJob(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Scheduled(cron = "0 0 * * *")
    public void removeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        passwordResetTokenRepository.deleteExpiredTokens(now);
    }
}
