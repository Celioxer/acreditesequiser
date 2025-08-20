package com.site.controllers;

import com.site.services.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/mercadopago")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // A notificação do Mercado Pago envia um payload com 'data'
            // que contém o 'id' do pagamento.
            if (payload.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                if (data.containsKey("id")) {
                    String paymentId = data.get("id").toString();
                    webhookService.processPaymentNotification(paymentId);
                    return ResponseEntity.ok("Notificação processada com sucesso");
                }
            }
            return ResponseEntity.badRequest().body("Payload de notificação inválido.");
        } catch (Exception e) {
            System.err.println("Erro ao processar o webhook: " + e.getMessage());
            return ResponseEntity.status(500).body("Erro interno ao processar a notificação.");
        }
    }
}
