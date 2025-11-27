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
            logger.info("Webhook recebido: {}", payload);

            if (!payload.containsKey("type") || !payload.containsKey("data")) {
                return ResponseEntity.badRequest().body("Formato inválido");
            }

            String type = payload.get("type").toString();
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            if (!data.containsKey("id")) {
                return ResponseEntity.badRequest().body("ID não encontrado");
            }

            String id = data.get("id").toString();

            switch (type) {
                case "payment":
                    webhookService.processPaymentNotification(id);
                    break;

                case "preapproval":
                    webhookService.processSubscriptionNotification(id);
                    break;

                default:
                    logger.warn("Webhook ignorado: tipo {}", type);
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            logger.error("Erro ao processar webhook", e);
            return ResponseEntity.status(500).body("Erro interno");
        }
    }
}
