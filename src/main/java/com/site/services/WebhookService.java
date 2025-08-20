package com.site.services;

import com.mercadopago.resources.payment.Payment;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.exceptions.MPApiException;
import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WebhookService {

    private final UsuarioRepository usuarioRepository;
    private final MercadoPagoService mercadoPagoService;

    public WebhookService(UsuarioRepository usuarioRepository, MercadoPagoService mercadoPagoService) {
        this.usuarioRepository = usuarioRepository;
        this.mercadoPagoService = mercadoPagoService;
    }

    /**
     * Processa uma notificação de pagamento do Mercado Pago.
     * @param paymentId O ID do pagamento enviado na notificação.
     */
    @Transactional
    public void processPaymentNotification(String paymentId) {
        try {
            // Obtém os detalhes completos do pagamento usando o ID.
            Payment payment = mercadoPagoService.getPayment(paymentId);

            // Verifique se o pagamento foi aprovado.
            if (payment != null && "approved".equals(payment.getStatus())) {
                String emailDoUsuario = payment.getPayer().getEmail();
                int planoDuracaoDias = 30; // Você pode obter isso do seu plano

                Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(emailDoUsuario);

                if (optionalUsuario.isPresent()) {
                    Usuario usuario = optionalUsuario.get();
                    LocalDateTime currentValidDate = usuario.getAcessoValidoAte();
                    LocalDateTime newValidDate;

                    if (currentValidDate == null || currentValidDate.isBefore(LocalDateTime.now())) {
                        newValidDate = LocalDateTime.now().plusDays(planoDuracaoDias);
                    } else {
                        newValidDate = currentValidDate.plusDays(planoDuracaoDias);
                    }

                    usuario.setAcessoValidoAte(newValidDate);
                    usuarioRepository.save(usuario);
                }
            }
        } catch (MPException | MPApiException e) {
            // Lida com erros de API do Mercado Pago
            System.err.println("Erro ao processar notificação de pagamento: " + e.getMessage());
        }
    }
}
