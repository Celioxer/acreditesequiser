package com.site.services;

import com.site.dto.PaymentRequestDTO;
import com.site.models.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MercadoPagoWebClientService {

    private final WebClient webClient;
    private final String accessToken;
    private final String baseUrl;

    public MercadoPagoWebClientService(
            @Value("${mercadopago.access.token}") String accessToken,
            @Value("${app.base-url}") String baseUrl
    ) {
        this.accessToken = accessToken;
        this.baseUrl = baseUrl;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.mercadopago.com")
                .build();
    }

    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        return fullName.trim().split("\\s+")[0];
    }

    private String getLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    // ==========================================================
    // 1. PAGAMENTO INICIAL (Mantido caso precise no futuro, mas não usado agora)
    // ==========================================================
    public Mono<Map<String, Object>> criarPagamentoInicial(
            Usuario usuario,
            PaymentRequestDTO dto
    ) {
        // ... (código igual, mantido por segurança) ...
        Map<String, Object> body = new HashMap<>();
        body.put("transaction_amount", dto.getValor());
        body.put("token", dto.getToken());
        body.put("description", dto.getDescricao() + " (Primeiro Pagamento)");
        body.put("installments", 1);
        if (dto.getPaymentMethodId() != null) body.put("payment_method_id", dto.getPaymentMethodId());
        if (dto.getIssuerId() != null) body.put("issuer_id", dto.getIssuerId());

        Map<String, Object> payerMap = new HashMap<>();
        payerMap.put("email", usuario.getEmail());
        payerMap.put("first_name", getFirstName(usuario.getNome()));
        payerMap.put("last_name", getLastName(usuario.getNome()));
        if(usuario.getCpf() != null) {
            payerMap.put("identification", Map.of("type", "CPF", "number", usuario.getCpf()));
        }
        body.put("payer", payerMap);
        body.put("external_reference", usuario.getId().toString());
        body.put("binary_mode", true);

        return webClient.post()
                .uri("/v1/payments")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // ==========================================================
    // 2. CRIAR ASSINATURA (CORRIGIDO: SEM FORÇAR STATUS)
    // ==========================================================
    public Mono<Map<String, Object>> criarAssinatura(
            Usuario usuario,
            String cardToken,
            String descricao,
            BigDecimal valor
    ) {

        Map<String, Object> autoRecurring = new HashMap<>();
        autoRecurring.put("frequency", 1);
        autoRecurring.put("frequency_type", "months");
        autoRecurring.put("transaction_amount", valor);
        autoRecurring.put("currency_id", "BRL");

        Map<String, Object> body = new HashMap<>();
        body.put("reason", descricao);
        body.put("auto_recurring", autoRecurring);
        body.put("payer_email", usuario.getEmail());
        body.put("card_token_id", cardToken);
        body.put("external_reference", usuario.getId().toString());

        // ❌ REMOVIDO: body.put("status", "authorized");
        // Isso permite que a assinatura nasça como 'pending' se o banco pedir validação,
        // evitando o erro 400.

        body.put("back_url", this.baseUrl + "/pagamento-sucesso");

        return webClient.post()
                .uri("/preapproval")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // ==========================================================
    // 3. PIX
    // ==========================================================
    public Mono<Map<String, Object>> iniciarPagamentoPix(
            Usuario usuario,
            String descricao,
            BigDecimal valor
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("transaction_amount", valor);
        body.put("description", descricao);
        body.put("payment_method_id", "pix");

        Map<String, Object> payer = new HashMap<>();
        payer.put("email", usuario.getEmail());
        payer.put("first_name", getFirstName(usuario.getNome()));
        payer.put("last_name", getLastName(usuario.getNome()));
        payer.put("identification", Map.of(
                "type", "CPF",
                "number", usuario.getCpf() != null ? usuario.getCpf() : ""
        ));

        body.put("payer", payer);
        body.put("external_reference", usuario.getId().toString());

        return webClient.post()
                .uri("/v1/payments")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // Consultas
    public Mono<Map<String, Object>> consultarPagamento(String paymentId) {
        return webClient.get()
                .uri("/v1/payments/" + paymentId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public Mono<Map<String, Object>> consultarPreapproval(String preapprovalId) {
        return webClient.get()
                .uri("/preapproval/" + preapprovalId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}