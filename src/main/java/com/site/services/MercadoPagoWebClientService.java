package com.site.services;

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

    // --- Métodos Auxiliares ---
    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) { return ""; }
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts[0];
    }

    private String getLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) { return ""; }
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    // ==========================================================
    // ✅ PIX (Corrigido com E-mail nulo)
    // ==========================================================
    public Mono<Map<String, Object>> iniciarPagamentoPix(
            com.site.models.Usuario usuario,
            String descricao,
            BigDecimal valor
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("transaction_amount", valor);
        body.put("description", descricao);
        body.put("payment_method_id", "pix");

        // --- INÍCIO DA CORREÇÃO (E-mail nulo) ---
        // Criamos o objeto 'payer' de forma segura, pois Map.of() não aceita nulos
        Map<String, Object> payerMap = new HashMap<>();
        payerMap.put("email", usuario.getEmail() != null ? usuario.getEmail() : ""); // <-- CORRIGIDO
        payerMap.put("first_name", getFirstName(usuario.getNome()));
        payerMap.put("last_name", getLastName(usuario.getNome()));
        payerMap.put("identification", Map.of(
                "type", "CPF",
                "number", usuario.getCpf() != null ? usuario.getCpf() : ""
        ));
        body.put("payer", payerMap);
        // --- FIM DA CORREÇÃO ---

        body.put("external_reference", usuario.getId().toString());

        return webClient.post()
                .uri("/v1/payments")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // ==========================================================
    // ✅ CARTÃO ÚNICO (Corrigido com E-mail nulo)
    // ==========================================================
    public Mono<Map<String, Object>> iniciarPagamentoCartao(
            com.site.models.Usuario usuario,
            String token,
            String descricao,
            BigDecimal valor,
            Integer installments,
            String paymentMethodId,
            Integer issuerId
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("transaction_amount", valor);
        body.put("token", token);
        body.put("description", descricao);
        body.put("installments", installments);
        body.put("payment_method_id", paymentMethodId);

        // --- INÍCIO DA CORREÇÃO (E-mail nulo) ---
        Map<String, Object> payerMap = new HashMap<>();
        payerMap.put("email", usuario.getEmail() != null ? usuario.getEmail() : ""); // <-- CORRIGIDO
        payerMap.put("first_name", getFirstName(usuario.getNome()));
        payerMap.put("last_name", getLastName(usuario.getNome()));
        payerMap.put("identification", Map.of(
                "type", "CPF",
                "number", usuario.getCpf() != null ? usuario.getCpf() : ""
        ));
        body.put("payer", payerMap);
        // --- FIM DA CORREÇÃO ---

        body.put("external_reference", usuario.getId().toString());
        if (issuerId != null) { body.put("issuer_id", issuerId); }

        return webClient.post()
                .uri("/v1/payments")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // ==========================================================
    // ⭐️ CRIAR ASSINATURA (Corrigido com 'back_url' e Idempotência)
    // ==========================================================
    public Mono<Map<String, Object>> criarAssinatura(
            com.site.models.Usuario usuario,
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

        // A API de Assinatura exige um e-mail. Se for nulo, a API vai retornar 400,
        // o que é melhor do que o nosso backend crashar com 500.
        body.put("payer_email", usuario.getEmail() != null ? usuario.getEmail() : "");

        body.put("card_token_id", cardToken);
        body.put("external_reference", usuario.getId().toString());
        body.put("status", "Authorized");
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
    // ✅ CONSULTAR PAGAMENTO (PIX / Cartão)
    // ==========================================================
    public Mono<Map<String, Object>> consultarPagamento(String paymentId) {
        return webClient.get()
                .uri("/v1/payments/" + paymentId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // ==========================================================
    // ✅ CONSULTAR ASSINATURA (preapproval)
    // ==========================================================
    public Mono<Map<String, Object>> consultarPreapproval(String preapprovalId) {
        return webClient.get()
                .uri("/preapproval/" + preapprovalId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}