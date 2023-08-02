package com.kliche.fit_user_api.repository;

import com.kliche.fit_user_api.model.PasswordResetToken;
import com.kliche.fit_user_api.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,String> {

    Optional<PasswordResetToken> findByToken(String token);
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiration_date <= ?1")
    void deleteExpiredTokens(LocalDateTime now);

    @Transactional
    void deleteByUser(User user);
}
