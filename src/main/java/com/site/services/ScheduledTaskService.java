package com.site.services;

import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.slf4j.Logger; // NOVO: Import para o logger
import org.slf4j.LoggerFactory; // NOVO: Import para o logger
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {

    // NOVO: Inicializa o logger para esta classe
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public ScheduledTaskService(UsuarioRepository usuarioRepository, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendSubscriptionExpirationReminders() {
        // ALTERADO: Usando logger.info em vez de System.out
        logger.info("------------------------------------------------------------");
        logger.info("INICIANDO TAREFA AGENDADA: Verificação de assinaturas a vencer...");

        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        LocalDate threeDaysFromNow = LocalDate.now().plusDays(3);

        logger.info("Procurando usuários com vencimento em 7 dias ({}).", sevenDaysFromNow);
        sendReminderForDate(sevenDaysFromNow);

        logger.info("Procurando usuários com vencimento em 3 dias ({}).", threeDaysFromNow);
        sendReminderForDate(threeDaysFromNow);

        logger.info("TAREFA AGENDADA CONCLUÍDA.");
        logger.info("------------------------------------------------------------");
    }

    private void sendReminderForDate(LocalDate targetDate) {
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atStartOfDay().plusDays(1).minusNanos(1); // Ajuste para pegar o dia inteiro

        List<Usuario> usersToExpire = usuarioRepository.findByAcessoValidoAteBetween(startOfDay, endOfDay);

        // NOVO: Log crucial para saber se usuários foram encontrados
        if (usersToExpire.isEmpty()) {
            logger.info("Nenhum usuário encontrado com assinatura vencendo em {}.", targetDate);
        } else {
            logger.info("Encontrados {} usuários com vencimento em {}. Enviando e-mails...", usersToExpire.size(), targetDate);
            for (Usuario user : usersToExpire) {
                try {
                    logger.info("-> Tentando enviar lembrete para: {}", user.getEmail());
                    emailService.sendSubscriptionReminderEmail(user);
                } catch (Exception e) {
                    // NOVO: Captura erros específicos do envio para este usuário
                    logger.error("Falha ao enviar e-mail de lembrete para {}. Erro: {}", user.getEmail(), e.getMessage());
                }
            }
        }
    }
}