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
    // ✅ PIX
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

        Map<String, Object> payerMap = new HashMap<>();
        payerMap.put("email", usuario.getEmail() != null ? usuario.getEmail() : "");
        payerMap.put("first_name", getFirstName(usuario.getNome()));
        payerMap.put("last_name", getLastName(usuario.getNome()));
        payerMap.put("identification", Map.of(
                "type", "CPF",
                "number", usuario.getCpf() != null ? usuario.getCpf() : ""
        ));
        body.put("payer", payerMap);

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
    // ✅ PAGAMENTO INICIAL (VALIDAÇÃO DO CARTÃO)
    // Ajustado para receber parâmetros individuais conforme seu Controller
    // ==========================================================
    public Mono<Map<String, Object>> criarPagamentoInicial(
            com.site.models.Usuario usuario,
            String token, // Agora aceita String token diretamente
            String descricao,
            BigDecimal valor,
            String deviceId // Mantido para compatibilidade de assinatura, mas não usado
    ) {
        // Se precisar usar o DTO internamente, podemos criar um, mas aqui usamos direto
        Map<String, Object> body = new HashMap<>();
        body.put("transaction_amount", valor);
        body.put("token", token);
        body.put("description", descricao + " (Primeiro Pagamento)");
        body.put("installments", 1);

        // --- CORREÇÃO: REMOVIDO payment_method_id E device_id ---
        // O Mercado Pago deduz a bandeira e banco pelo token.
        // Enviar "credit_card" fixo ou IDs manuais causa diff_param_bins.
        // body.put("payment_method_id", "credit_card");

        // device_id removido do body para evitar erro 400 da API

        Map<String, Object> payerMap = new HashMap<>();
        payerMap.put("email", usuario.getEmail() != null ? usuario.getEmail() : "");
        payerMap.put("first_name", getFirstName(usuario.getNome()));
        payerMap.put("last_name", getLastName(usuario.getNome()));

        if (usuario.getCpf() != null) {
            payerMap.put("identification", Map.of(
                    "type", "CPF",
                    "number", usuario.getCpf()
            ));
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

    // Sobrecarga para aceitar DTO se necessário no futuro
    public Mono<Map<String, Object>> criarPagamentoInicial(Usuario usuario, PaymentRequestDTO dto) {
        return criarPagamentoInicial(usuario, dto.getToken(), dto.getDescricao(), dto.getValor(), null);
    }

    // ==========================================================
    // ⭐️ CRIAR ASSINATURA (CORRIGIDO: Sem forçar 'Authorized')
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
        body.put("payer_email", usuario.getEmail() != null ? usuario.getEmail() : "");
        body.put("card_token_id", cardToken);
        body.put("external_reference", usuario.getId().toString());

        // REMOVIDO: body.put("status", "Authorized");

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