package com.site.services;

import com.site.models.Usuario;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
public class EmailService {

    // NOVO: Inicializa o logger para esta classe
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    // --- INÍCIO DAS PROPRIEDADES INJETADAS ---

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.admin-emails}")
    private String[] adminEmailAddresses;

    // --- FIM DAS PROPRIEDADES INJETADAS ---

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Redefinição de Senha - Acredite Se Quiser");
        message.setText("Para redefinir sua senha, clique no link abaixo:\n" + resetUrl
                + "\n\nSe você não solicitou isso, por favor ignore este email.");

        try {
            logger.info("Tentando enviar e-mail de redefinição de senha para {}", to);
            mailSender.send(message);
            logger.info("E-mail de redefinição de senha enviado com SUCESSO para {}", to);
        } catch (MailException e) {
            logger.error("ERRO ao enviar e-mail de redefinição de senha para {}: {}", to, e.getMessage());
            throw e; // Relança a exceção para notificar a camada de serviço que chamou
        }
    }

    public void sendSubscriptionReminderEmail(Usuario usuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(usuario.getEmail());
        message.setSubject("Lembrete: Sua assinatura está prestes a vencer!");

        String expiryDate = usuario.getAcessoValidoAte().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        message.setText("Olá, " + usuario.getNome() + "!\n\n"
                + "Sua assinatura de apoiador do Acredite Se Quiser está programada para vencer no dia " + expiryDate + ".\n\n"
                + "Para garantir que você não perca o acesso ao conteúdo exclusivo e ao nosso grupo, renove sua assinatura em nosso site.\n\n"
                + "Agradecemos por fazer parte da nossa comunidade!\n\n"
                + "Atenciosamente,\nEquipe Acredite Se Quiser");

        try {
            logger.info("Tentando enviar e-mail de lembrete de assinatura para {}", usuario.getEmail());
            mailSender.send(message);
            logger.info("E-mail de lembrete enviado com SUCESSO para {}", usuario.getEmail());
        } catch (MailException e) {
            logger.error("ERRO ao enviar e-mail de lembrete para {}: {}", usuario.getEmail(), e.getMessage());
            throw e;
        }
    }

    public void sendNewSubscriberNotification(String newSubscriberEmail) {
        if (adminEmailAddresses == null || adminEmailAddresses.length == 0) {
            // ALTERADO: Usando logger.warn para avisos
            logger.warn("AVISO: Nenhum e-mail de administrador (app.admin-emails) configurado. Notificação de novo assinante não enviada.");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(adminEmailAddresses);
        message.setSubject("🎉 Novo Assinante na Plataforma!");
        message.setText("Olá! Um novo usuário se tornou assinante.\n\nE-mail do novo Membro: " + newSubscriberEmail);

        try {
            logger.info("Tentando enviar notificação de novo assinante para os administradores: {}", Arrays.toString(adminEmailAddresses));
            mailSender.send(message);
            // ALTERADO: Usando logger.info para registrar o sucesso
            logger.info("Notificação de novo assinante enviada com SUCESSO para: {}", Arrays.toString(adminEmailAddresses));
        } catch (MailException e) {
            logger.error("ERRO ao enviar notificação de novo assinante para {}: {}", Arrays.toString(adminEmailAddresses), e.getMessage());
            throw e;
        }
    }

    public void sendWelcomeEmailHtml(String to, String subject, String htmlContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            logger.info("Tentando enviar e-mail HTML '{}' para {}", subject, to);
            mailSender.send(mimeMessage);
            logger.info("E-mail HTML '{}' enviado com SUCESSO para {}", subject, to);
        } catch (MessagingException | MailException e) { // Captura ambas as exceções
            // ALTERADO: Usando logger.error para registrar a falha
            logger.error("ERRO ao enviar e-mail HTML para {}: {}", to, e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail HTML", e);
        }
    }

    public void sendCustomEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            logger.info("Tentando enviar e-mail customizado '{}' para {}", subject, to);
            mailSender.send(message);
            logger.info("E-mail customizado '{}' enviado com SUCESSO para {}", subject, to);
        } catch (MailException e) {
            logger.error("ERRO ao enviar e-mail customizado para {}: {}", to, e.getMessage());
            throw e;
        }
    }
}