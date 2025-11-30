package com.site.services;

import com.site.models.PaymentHistory;
import com.site.repositories.PaymentHistoryRepository;
import com.site.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final MercadoPagoWebClientService mercadoPagoWebClientService;
    private final UsuarioService usuarioService;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public WebhookService(
            MercadoPagoWebClientService mercadoPagoWebClientService,
            UsuarioService usuarioService,
            PaymentHistoryRepository paymentHistoryRepository
    ) {
        this.mercadoPagoWebClientService = mercadoPagoWebClientService;
        this.usuarioService = usuarioService;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    // =====================================================
    // ✅ PROCESSAR PAGAMENTO PIX/CARTÃO (type=payment)
    // =====================================================
    @Transactional
    public void processPaymentNotification(String paymentId) {

        if (paymentHistoryRepository.existsByPaymentId(paymentId)) {
            logger.warn("Pagamento {} já registrado. Ignorado.", paymentId);
            return;
        }

        try {
            Map<String, Object> response = mercadoPagoWebClientService.consultarPagamento(paymentId).block();

            if (response == null) {
                logger.error("Resposta nula do Mercado Pago para o paymentId: {}", paymentId);
                return;
            }

            logger.info("🔎 Processando 'payment' ID: {}. Detalhes: {}", paymentId, response);

            // --- INÍCIO DA CORREÇÃO DO NULLPOINTEREXCEPTION ---

            Object statusObj = response.get("status");
            if (statusObj == null) {
                logger.warn("Webhook 'payment' ID: {} recebido sem o campo 'status'. Ignorado.", paymentId);
                return;
            }
            String status = statusObj.toString();

            Object externalRefObj = response.get("external_reference");
            if (externalRefObj == null) {
                logger.warn("Webhook 'payment' ID: {} recebido sem o campo 'external_reference'. Ignorado.", paymentId);
                return;
            }
            String externalRef = externalRefObj.toString(); // Esta era a antiga LINHA 58

            // --- FIM DA CORREÇÃO DO NULLPOINTEREXCEPTION ---

            // --- TRATAMENTO PARA GARANTIR QUE external_reference É UM ID DE USUÁRIO ---
            Long usuarioId;
            try {
                usuarioId = Long.parseLong(externalRef);
            } catch (NumberFormatException e) {
                logger.warn("Webhook 'payment' recebido com external_reference inválida (não é um número): '{}'. Ignorando.", externalRef);
                return; // Para de processar este webhook, pois não é de um usuário
            }
            // --- FIM DO TRATAMENTO ---

            if ("approved".equals(status)) {

                // Extração segura de campos aninhados:
                Map<String, Object> paymentMethod = (Map<String, Object>) response.get("payment_method");
                String paymentMethodId = (paymentMethod != null && paymentMethod.get("id") != null)
                        ? paymentMethod.get("id").toString() : "UNKNOWN";

                BigDecimal amount = (response.get("transaction_amount") != null)
                        ? new BigDecimal(response.get("transaction_amount").toString())
                        : BigDecimal.ZERO;

                String statusDetail = (response.get("status_detail") != null)
                        ? response.get("status_detail").toString()
                        : "N/A";

                LocalDateTime dateApproved = LocalDateTime.now();
                if (response.get("date_approved") != null) {
                    String dateApprovedStr = response.get("date_approved").toString();
                    dateApproved = OffsetDateTime.parse(dateApprovedStr).toLocalDateTime();
                }

                Integer installments = response.get("installments") != null ? ((Number) response.get("installments")).intValue() : null;

                PaymentHistory ph = new PaymentHistory(
                        paymentId,
                        usuarioId,
                        amount,
                        dateApproved,
                        paymentMethodId,
                        status,
                        statusDetail,
                        installments
                );
                paymentHistoryRepository.save(ph);

                usuarioService.liberarAssinatura(usuarioId, 30);
                logger.info("✅ Acesso liberado/renovado para usuário ID: {}", usuarioId);

            } else {
                logger.warn("Pagamento {} não aprovado (Status: {}). Acesso não liberado.", paymentId, status);
            }

        } catch (Exception e) {
            logger.error("Erro ao processar notificação de pagamento {}. Transação será revertida.", paymentId, e);
            throw new RuntimeException("Falha ao processar pagamento " + paymentId, e);
        }
    }

    // =====================================================
    // ✅ PROCESSAR MUDANÇA DE STATUS DA ASSINATURA (type=preapproval)
    //    (Corrigido para segurança contra NPE)
    // =====================================================
    @Transactional
    public void processSubscriptionNotification(String preapprovalId) {

        try {
            Map<String, Object> response = mercadoPagoWebClientService.consultarPreapproval(preapprovalId).block();

            if (response == null) {
                logger.error("Resposta nula do Mercado Pago para o preapprovalId: {}", preapprovalId);
                return;
            }

            logger.info("🔎 Processando 'preapproval' ID: {}. Detalhes: {}", preapprovalId, response);

            // --- INÍCIO DA CORREÇÃO DO NULLPOINTEREXCEPTION (status e external_reference) ---

            Object statusObj = response.get("status");
            if (statusObj == null) {
                logger.warn("Webhook 'preapproval' ID: {} recebido sem o campo 'status'. Ignorado.", preapprovalId);
                return;
            }
            String status = statusObj.toString();

            Object externalRefObj = response.get("external_reference");
            if (externalRefObj == null) {
                logger.warn("Webhook 'preapproval' ID: {} recebido sem o campo 'external_reference'. Ignorado.", preapprovalId);
                return;
            }
            String externalRef = externalRefObj.toString();

            // --- FIM DA CORREÇÃO DO NULLPOINTEREXCEPTION ---

            // --- TRATAMENTO PARA GARANTIR QUE external_reference É UM ID DE USUÁRIO ---
            Long usuarioId;
            try {
                usuarioId = Long.parseLong(externalRef);
            } catch (NumberFormatException e) {
                logger.warn("Webhook 'preapproval' recebido com external_reference inválida (não é um número): '{}'. Ignorando.", externalRef);
                return; // Para de processar este webhook
            }
            // --- FIM DO TRATAMENTO ---

            switch (status) {
                case "paused":
                case "cancelled":
                    usuarioService.removerAssinatura(usuarioId);
                    logger.info("⚠ Assinatura {} cancelada/pausada para usuário ID: {}", preapprovalId, usuarioId);
                    break;
                case "authorized":
                default:
                    logger.info("Assinatura {} com status: {}. Nenhuma ação necessária.", preapprovalId, status);
            }

        } catch (Exception e) {
            logger.error("Erro ao processar notificação de assinatura {}. Transação será revertida.", preapprovalId, e);
            throw new RuntimeException("Falha ao processar assinatura " + preapprovalId, e);
        }
    }
}