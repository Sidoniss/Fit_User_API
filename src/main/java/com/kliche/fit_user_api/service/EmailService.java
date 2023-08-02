package com.kliche.fit_user_api.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendPasswordResetEmail(String recipientEmail,String resetToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject("Reset hasła");
        mailMessage.setText("Witaj!\n\nOtrzymałeś ten e-mail, ponieważ zażądałeś resetu hasła. Oto twój unikalny token resetu hasła:\n\n"
        + resetToken + "\n\nProsimy o zachowanie tego tokenu w poufności i nie udostępnianie go innym osobom.\n\n"
        + "Jeśli to nie Ty zażądałeś resetu hasła, zignoruj ten e-mail.\n\n"
        + "Pozdrawiamy,\nFitApp");

        javaMailSender.send(mailMessage);
    }
}
