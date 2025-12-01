package com.site.services;

import com.site.models.PaymentHistory;
import com.site.repositories.PaymentHistoryRepository;
// import com.site.models.Usuario; // Se precisar importar
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
            // Busca detalhes no Mercado Pago
            Map<String, Object> response = mercadoPagoWebClientService.consultarPagamento(paymentId).block();

            if (response == null) {
                logger.error("Resposta nula do Mercado Pago para o paymentId: {}", paymentId);
                return;
            }

            logger.info("🔎 Processando 'payment' ID: {}. Detalhes: {}", paymentId, response);

            // --- PROTEÇÃO CONTRA NULL POINTER EXCEPTION ---
            Object statusObj = response.get("status");
            if (statusObj == null) {
                logger.warn("Webhook 'payment' ID: {} recebido sem o campo 'status'. Ignorado.", paymentId);
                return;
            }
            String status = statusObj.toString();

            Object externalRefObj = response.get("external_reference");
            if (externalRefObj == null) {
                logger.warn("Webhook 'payment' ID: {} sem 'external_reference'. Ignorado.", paymentId);
                return;
            }
            String externalRef = externalRefObj.toString();
            // ----------------------------------------------

            // Tenta converter external_reference para ID de usuário
            Long usuarioId;
            try {
                usuarioId = Long.parseLong(externalRef);
            } catch (NumberFormatException e) {
                // Se for "Recurring payment validation", cai aqui e é ignorado corretamente.
                logger.warn("Webhook 'payment' com external_reference inválida: '{}'. Ignorando.", externalRef);
                return;
            }

            if ("approved".equals(status)) {

                // Extração segura de dados
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
                        logger.warn("Erro ao parsear data, usando now()", e);
                    }
                }

                Integer installments = response.get("installments") != null
                        ? ((Number) response.get("installments")).intValue()
                        : 1;

                // =========================================================
                // PASSO 2 DO FLUXO DE CARTÃO: CRIAR A ASSINATURA
                // =========================================================
                if ("credit_card".equalsIgnoreCase(paymentMethodId) || "credit_card".equalsIgnoreCase((String) response.get("payment_type_id"))) {
                    logger.info("Pagamento de cartão aprovado. Tentando criar assinatura para usuário {}...", usuarioId);

                    // Se o seu fluxo principal já cria a assinatura no Controller (quando approved),
                    // aqui nós apenas registramos o pagamento e liberamos o acesso.
                    // O MP vai cobrar a assinatura mês que vem automaticamente.

                    // IMPORTANTE: Se o pagamento inicial foi criado com sucesso no controller,
                    // a assinatura JÁ DEVERIA ter sido criada lá (se foi approved imediato).
                    // Se ficou 'in_process' e aprovou agora, criar a assinatura aqui seria ideal,
                    // mas precisaria do 'card_token', que o webhook NÃO TRAZ.
                    // Nesse caso, o usuário terá acesso por 30 dias (pelo pagamento avulso aprovado),
                    // mas terá que renovar manualmente mês que vem. É um compromisso aceitável.
                }

                // Salva histórico
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

                // Libera acesso
                usuarioService.liberarAssinatura(usuarioId, 30);
                logger.info("✅ Acesso liberado/renovado para usuário ID: {}", usuarioId);

            } else {
                logger.warn("Pagamento {} não aprovado (Status: {}). Acesso não liberado.", paymentId, status);
            }

        } catch (Exception e) {
            logger.error("Erro ao processar notificação de pagamento {}", paymentId, e);
            // Não lançamos exceção para não travar o webhook do MP em loop infinito de retry
        }
    }

    // =====================================================
    // ✅ PROCESSAR MUDANÇA DE STATUS DA ASSINATURA (preapproval)
    // =====================================================
    @Transactional
    public void processSubscriptionNotification(String preapprovalId) {

        try {
            Map<String, Object> response = mercadoPagoWebClientService.consultarPreapproval(preapprovalId).block();

            if (response == null) {
                logger.error("Resposta nula do Mercado Pago para preapprovalId: {}", preapprovalId);
                return;
            }

            logger.info("🔎 Processando 'preapproval' ID: {}. Detalhes: {}", preapprovalId, response);

            // Proteção contra NPE
            Object statusObj = response.get("status");
            if (statusObj == null) return;
            String status = statusObj.toString();

            Object externalRefObj = response.get("external_reference");
            if (externalRefObj == null) return;

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
                    logger.info("⚠ Assinatura {} cancelada/pausada. Acesso removido para usuário: {}", preapprovalId, usuarioId);
                    break;
                case "authorized":
                    // O acesso geralmente é liberado pelo pagamento (payment), mas podemos reforçar aqui
                    // usuarioService.liberarAssinatura(usuarioId, 30);
                    logger.info("Assinatura {} ativa/autorizada.", preapprovalId);
                    break;
            }

        } catch (Exception e) {
            logger.error("Erro ao processar notificação de assinatura {}", preapprovalId, e);
        }
    }
}