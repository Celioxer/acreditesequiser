package com.site.controllers;

import com.site.services.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mercadopago")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // Log discreto para não poluir, mas útil para debug
            logger.info("Webhook recebido: topic={}, id={}", payload.get("topic"), payload.get("id"));

            // O Mercado Pago pode enviar "topic" ou "type" dependendo da versão da API
            String topic = null;
            if (payload.containsKey("topic")) {
                topic = (String) payload.get("topic");
            } else if (payload.containsKey("type")) {
                topic = (String) payload.get("type");
            }

            // Extrai o ID do recurso (pagamento ou assinatura)
            String resourceId = null;
            if (payload.containsKey("data") && payload.get("data") instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                if (data.containsKey("id")) {
                    resourceId = data.get("id").toString();
                }
            } else if (payload.containsKey("id")) {
                // Alguns webhooks antigos mandam o ID na raiz
                resourceId = payload.get("id").toString();
            }

            if (resourceId == null) {
                logger.warn("Webhook ignorado: ID não encontrado no payload: {}", payload);
                return ResponseEntity.ok("Ignored (No ID)");
            }

            // Roteamento baseado no tópico
            if ("payment".equals(topic)) {
                logger.info("Processando notificação de PAGAMENTO. ID: {}", resourceId);
                webhookService.processPaymentNotification(resourceId);
            } else if ("preapproval".equals(topic) || "subscription_preapproval".equals(topic)) {
                logger.info("Processando notificação de ASSINATURA. ID: {}", resourceId);
                webhookService.processSubscriptionNotification(resourceId);
            } else {
                logger.debug("Tópico ignorado: {}", topic);
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            logger.error("Erro ao processar webhook", e);
            // Retornamos OK para o Mercado Pago não ficar tentando reenviar se for um erro interno nosso que não vai se resolver
            return ResponseEntity.status(500).body("Erro interno");
        }
    }
}