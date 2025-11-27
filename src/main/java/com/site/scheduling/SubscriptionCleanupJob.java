package com.site.scheduling;

import com.site.models.Usuario;
import com.site.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionCleanupJob {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionCleanupJob.class);

    private final UsuarioService usuarioService;

    public SubscriptionCleanupJob(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Esta tarefa é executada todos os dias às 03:00.
     * Ela verifica todos os usuários 'SUBSCRIBER' cujo acesso expirou
     * e altera o Role para 'USER'.
     */
    // Cron expression para 03:00 AM (0 0 3 * * *)
    // Use fixedRate se quiser uma frequência mais simples, ex: @Scheduled(fixedRate = 86400000) (a cada 24h)
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredSubscriptions() {
        logger.info("Iniciando varredura de assinaturas expiradas...");
        long count = usuarioService.desativarAssinaturasVencidas();

        logger.info("Varredura de assinaturas finalizada. Total de {} usuários desativados.", count);
    }
}