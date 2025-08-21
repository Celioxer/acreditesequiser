package com.site.services;

import com.mercadopago.resources.payment.Payment;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.exceptions.MPApiException;
import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final UsuarioRepository usuarioRepository;
    private final MercadoPagoService mercadoPagoService;
    private final PaymentHistoryService paymentHistoryService; // tabela auxiliar para evitar duplicação

    public WebhookService(UsuarioRepository usuarioRepository,
                          MercadoPagoService mercadoPagoService,
                          PaymentHistoryService paymentHistoryService) {
        this.usuarioRepository = usuarioRepository;
        this.mercadoPagoService = mercadoPagoService;
        this.paymentHistoryService = paymentHistoryService;
    }

    /**
     * Processa uma notificação de pagamento do Mercado Pago.
     * @param paymentId O ID do pagamento enviado na notificação.
     */
    @Transactional
    public void processPaymentNotification(String paymentId) {
        try {
            // Evita processar a mesma notificação duas vezes
            if (paymentHistoryService.existsByPaymentId(paymentId)) {
                logger.info("Pagamento {} já processado, ignorando...", paymentId);
                return;
            }

            // Obtém os detalhes completos do pagamento
            Payment payment = mercadoPagoService.getPayment(paymentId);

            if (payment != null && "approved".equalsIgnoreCase(payment.getStatus())) {

                // Pegamos o usuário pela external_reference, não pelo e-mail
                String userIdRef = payment.getExternalReference();

                Optional<Usuario> optionalUsuario = usuarioRepository.findById(Long.valueOf(userIdRef));

                if (optionalUsuario.isPresent()) {
                    Usuario usuario = optionalUsuario.get();
                    int planoDuracaoDias = 30;

                    LocalDateTime currentValidDate = usuario.getAcessoValidoAte();
                    LocalDateTime newValidDate;

                    if (currentValidDate == null || currentValidDate.isBefore(LocalDateTime.now())) {
                        newValidDate = LocalDateTime.now().plusDays(planoDuracaoDias);
                    } else {
                        newValidDate = currentValidDate.plusDays(planoDuracaoDias);
                    }

                    usuario.setAcessoValidoAte(newValidDate);
                    usuarioRepository.save(usuario);

                    paymentHistoryService.savePayment(paymentId, usuario.getId()); // marca como processado

                    logger.info("Pagamento {} aprovado. Usuário {} atualizado até {}",
                            paymentId, usuario.getEmail(), newValidDate);
                } else {
                    logger.warn("Usuário não encontrado para external_reference {}", userIdRef);
                }
            } else {
                logger.info("Pagamento {} recebido mas status: {}",
                        paymentId, payment != null ? payment.getStatus() : "NULO");
            }
        } catch (MPException | MPApiException e) {
            logger.error("Erro ao processar notificação de pagamento {}: {}", paymentId, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao processar notificação {}: {}", paymentId, e.getMessage(), e);
        }
    }
}
