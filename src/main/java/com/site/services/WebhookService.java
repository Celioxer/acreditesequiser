package com.site.services;

import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class WebhookService {

    private final UsuarioRepository usuarioRepository;

    public WebhookService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void processPaymentNotification(String payload) {
        // Esta é uma lógica simplificada.
        // Na vida real, você precisaria verificar a assinatura do webhook
        // e obter os detalhes do pagamento do payload JSON.

        // Exemplo: Simular a obtenção do email do usuário e o valor pago
        String emailDoUsuario = "usuario@teste.com"; // Deve vir do payload

        // Lógica de negócio:
        // Se o plano for o de 30 reais, adicione 30 dias.
        // Se for o de 60 reais, adicione 30 dias e talvez um status de "premium".

        usuarioRepository.findByEmail(emailDoUsuario).ifPresent(usuario -> {
            usuario.setAcessoValidoAte(LocalDateTime.now().plusDays(30));
            usuarioRepository.save(usuario);
        });
    }
}