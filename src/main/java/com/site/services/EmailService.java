package com.site.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com"); // Deve ser o mesmo do application.properties
        message.setTo(to);
        message.setSubject("Redefinição de Senha - Acredite Se Quiser");
        message.setText("Para redefinir sua senha, clique no link abaixo:\n" + resetUrl
                + "\n\nSe você não solicitou isso, por favor ignore este email.");
        mailSender.send(message);
    }
}