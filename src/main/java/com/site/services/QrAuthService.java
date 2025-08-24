package com.site.services;

import com.site.dto.QrStatusResponse;
import com.site.models.QrSession; // Criaremos esta classe a seguir
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QrAuthService {

    // Para produção, o ideal é usar um banco de dados rápido como Redis com TTL (tempo de expiração).
    // Por enquanto, um Mapa em memória é suficiente para fazer funcionar.
    private final Map<String, QrSession> activeSessions = new ConcurrentHashMap<>();

    /**
     * Cria uma nova sessão de QR code com status PENDENTE.
     * @return O token único da sessão.
     */
    public String createPendingSession() {
        String sessionToken = UUID.randomUUID().toString();
        QrSession session = new QrSession();
        activeSessions.put(sessionToken, session);
        return sessionToken;
    }

    /**
     * Verifica o status de uma sessão.
     * @param sessionToken O token da sessão a ser verificada.
     * @return Um objeto com o status atual e o token de autenticação final (se disponível).
     */
    public QrStatusResponse getSessionStatus(String sessionToken) {
        QrSession session = activeSessions.get(sessionToken);

        if (session == null) {
            // A sessão não existe ou já expirou
            return new QrStatusResponse(QrSession.Status.EXPIRED, null);
        }

        return new QrStatusResponse(session.getStatus(), session.getFinalAuthToken());
    }

    /**
     * Chamado pelo app mobile para confirmar o login.
     * @param sessionToken O token da sessão vindo do QR code.
     * @param userId O ID do usuário que está logado no app mobile.
     */
    public void confirmSession(String sessionToken, String userId) {
        QrSession session = activeSessions.get(sessionToken);

        // A sessão deve existir e estar pendente
        if (session != null && session.getStatus() == QrSession.Status.PENDING) {
            session.setStatus(QrSession.Status.CONFIRMED);
            session.setUserId(userId);

            // IMPORTANTE: Aqui você deve gerar o token de acesso final (JWT, por exemplo)
            // que será enviado para o navegador do usuário.
            String finalJwtToken = "GERAR_TOKEN_JWT_PARA_O_USUARIO_" + userId;
            session.setFinalAuthToken(finalJwtToken);

            // Atualiza a sessão no mapa
            activeSessions.put(sessionToken, session);
        }
    }
}