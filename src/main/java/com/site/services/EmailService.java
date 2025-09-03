package com.site.services;

import com.site.models.Usuario; //
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("phcarvalho76@gmail.com");
        message.setTo(to);
        message.setSubject("Redefinição de Senha - Acredite Se Quiser");
        message.setText("Para redefinir sua senha, clique no link abaixo:\n" + resetUrl
                + "\n\nSe você não solicitou isso, por favor ignore este email.");
        mailSender.send(message);
    }

    public void sendSubscriptionReminderEmail(Usuario usuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("phcarvalho76@gmail.com");
        message.setTo(usuario.getEmail());
        message.setSubject("Lembrete: Sua assinatura está prestes a vencer!");

        String expiryDate = usuario.getAcessoValidoAte().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        message.setText("Olá, " + usuario.getNome() + "!\n\n"
                + "Sua assinatura de apoiador do Acredite Se Quiser está programada para vencer no dia " + expiryDate + ".\n\n"
                + "Para garantir que você não perca o acesso ao conteúdo exclusivo e ao nosso grupo, renove seu apoio em nosso site.\n\n"
                + "Agradecemos por fazer parte da nossa comunidade!\n\n"
                + "Atenciosamente,\nEquipe Acredite Se Quiser");

        mailSender.send(message);
    }

    public void sendNewSubscriberNotification(String adminEmail, String newSubscriberEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("phcarvalho76@gmail.com");
        message.setTo(adminEmail);
        message.setSubject("🎉 Novo Assinante na Plataforma!");
        message.setText("Olá! Um novo usuário se tornou assinante.\n\nE-mail do novo apoiador: " + newSubscriberEmail);
        mailSender.send(message);
    }

    public void sendWelcomeEmailHtml(String to, String subject, String htmlContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("phcarvalho76@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar e-mail HTML", e);
        }
    }

    public void sendCustomEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("phcarvalho76@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}