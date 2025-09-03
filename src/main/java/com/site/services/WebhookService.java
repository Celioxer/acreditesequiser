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
    private final PaymentHistoryService paymentHistoryService;

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
            if (paymentHistoryService.existsByPaymentId(paymentId)) {
                logger.info("Pagamento {} já processado, ignorando...", paymentId);
                return;
            }

            Payment payment = mercadoPagoService.getPayment(paymentId);

            if (payment != null && "approved".equalsIgnoreCase(payment.getStatus())) {

                String userIdRef = payment.getExternalReference();
                Optional<Usuario> optionalUsuario = usuarioRepository.findById(Long.valueOf(userIdRef));

                if (optionalUsuario.isPresent()) {
                    Usuario usuario = optionalUsuario.get();

                    // ****** ALTERAÇÕES PRINCIPAIS AQUI ******

                    // 1. Lógica de data ajustada para NÃO acumular dias.
                    // A validade é sempre 30 dias a partir do dia do pagamento.
                    int planoDuracaoDias = 30;
                    LocalDateTime newValidDate = LocalDateTime.now().plusDays(planoDuracaoDias);
                    usuario.setAcessoValidoAte(newValidDate);

                    // 2. Adiciona a permissão de assinante para liberar o acesso.
                    // Isso corrige o problema do usuário não conseguir acessar o conteúdo.
                    usuario.setRole(Usuario.Role.SUBSCRIBER);

                    // 3. Salva o usuário com a data e a permissão atualizadas.
                    usuarioRepository.save(usuario);

                    // *****************************************

                    paymentHistoryService.savePayment(
                            paymentId,
                            usuario.getId(),
                            payment.getTransactionAmount(),
                            payment.getDateApproved().toLocalDateTime(),
                            payment.getPaymentMethodId(),
                            payment.getStatus(),
                            payment.getStatusDetail(),
                            payment.getInstallments()
                    );

                    logger.info("Pagamento {} aprovado. Usuário {} atualizado para SUBSCRIBER até {}",
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