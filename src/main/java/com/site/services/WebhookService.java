package com.site.services;

import com.site.models.PaymentHistory;
import com.site.repositories.PaymentHistoryRepository;
// import com.site.models.Usuario;
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

            // --- CORREÇÃO DO ERRO (NullPointerException) AQUI ---

            // 1. Verifica se o STATUS existe antes de converter
            Object statusObj = response.get("status");
            if (statusObj == null) {
                logger.warn("Webhook 'payment' ID: {} ignorado: campo 'status' ausente.", paymentId);
                return;
            }
            String status = statusObj.toString();

            // 2. Verifica se a EXTERNAL_REFERENCE existe antes de converter
            Object externalRefObj = response.get("external_reference");
            if (externalRefObj == null) {
                logger.warn("Webhook 'payment' ID: {} ignorado: campo 'external_reference' ausente (provavelmente teste de validação).", paymentId);
                return;
            }
            String externalRef = externalRefObj.toString();

            // ----------------------------------------------------

            Long usuarioId;
            try {
                usuarioId = Long.parseLong(externalRef);
            } catch (NumberFormatException e) {
                logger.warn("Webhook 'payment' ignorado: external_reference inválida ('{}').", externalRef);
                return;
            }

            if ("approved".equals(status)) {

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
                    try {
                        String dateApprovedStr = response.get("date_approved").toString();
                        dateApproved = OffsetDateTime.parse(dateApprovedStr).toLocalDateTime();
                    } catch (Exception e) {
                        logger.warn("Erro ao converter data, usando hora atual.");
                    }
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

            // --- PROTEÇÃO CONTRA NULL AQUI TAMBÉM ---
            Object statusObj = response.get("status");
            if (statusObj == null) return;
            String status = statusObj.toString();

            Object externalRefObj = response.get("external_reference");
            if (externalRefObj == null) return;
            // ----------------------------------------

            Long usuarioId;
            try {
                usuarioId = Long.parseLong(externalRefObj.toString());
            } catch (NumberFormatException e) {
                return;
            }

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
            logger.error("Erro ao processar notificação de assinatura {}.", preapprovalId, e);
        }
    }
}