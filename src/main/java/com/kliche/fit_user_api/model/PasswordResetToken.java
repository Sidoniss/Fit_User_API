package com.kliche.fit_user_api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    @JoinColumn(nullable = false,name = "user_id")
    private User user;
    @Column
    private String token;
    @Column
    private LocalDateTime expiration_date;

    public PasswordResetToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.expiration_date = LocalDateTime.now().plusMinutes(30);
    }

    public PasswordResetToken() {
        this.expiration_date = LocalDateTime.now().plusMinutes(30);
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(expiration_date);
    }
}
