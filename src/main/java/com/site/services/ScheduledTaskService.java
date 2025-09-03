package com.site.services;

import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public ScheduledTaskService(UsuarioRepository usuarioRepository, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    /**
     * Esta tarefa roda todos os dias às 9h da manhã.
     * A sintaxe é um "cron expression": (segundo, minuto, hora, dia do mês, mês, dia da semana)
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendSubscriptionExpirationReminders() {
        System.out.println("Executando tarefa agendada: Verificando assinaturas a vencer...");

        // Envia lembrete para quem vence em 7 dias
        sendReminderForDate(LocalDate.now().plusDays(7));

        // Envia lembrete para quem vence em 3 dias
        sendReminderForDate(LocalDate.now().plusDays(3));
    }

    private void sendReminderForDate(LocalDate targetDate) {
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.plusDays(1).atStartOfDay();

        List<Usuario> usersToExpire = usuarioRepository.findByAcessoValidoAteBetween(startOfDay, endOfDay);

        for (Usuario user : usersToExpire) {
            // Você precisará criar este novo método no seu EmailService
            emailService.sendSubscriptionReminderEmail(user);
        }
    }
}