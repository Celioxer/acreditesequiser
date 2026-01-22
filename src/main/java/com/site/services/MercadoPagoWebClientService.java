package com.site.services;

import com.site.dto.PaymentRequestDTO;
import com.site.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MercadoPagoWebClientService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoWebClientService.class);
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
        if (fullName == null || fullName.trim().isEmpty()) return "Cliente";
        return fullName.trim().split("\\s+")[0];
    }

    private String getLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    // ==================================================================================
    // 🆕 PASSO 1: OBTER OU CRIAR CLIENTE (CUSTOMER) NO MERCADO PAGO
    // ==================================================================================
    public Mono<String> getOrCreateCustomer(Usuario usuario) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/customers/search")
                        .queryParam("email", usuario.getEmail())
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .flatMap(response -> {
                    List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                    if (results != null && !results.isEmpty()) {
                        String existingId = (String) results.get(0).get("id");
                        logger.info("Cliente encontrado no MP: {}", existingId);
                        return Mono.just(existingId);
                    } else {
                        return criarNovoCustomer(usuario);
                    }
                });
    }

    private Mono<String> criarNovoCustomer(Usuario usuario) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", usuario.getEmail());
        body.put("first_name", getFirstName(usuario.getNome()));
        body.put("last_name", getLastName(usuario.getNome()));

        if (usuario.getCpf() != null) {
            body.put("identification", Map.of("type", "CPF", "number", usuario.getCpf()));
        }

        return webClient.post()
                .uri("/v1/customers")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(resp -> {
                    String newId = (String) resp.get("id");
                    logger.info("Novo cliente criado no MP: {}", newId);
                    return newId;
                });
    }

    // ==================================================================================
    // 🆕 PASSO 2: SALVAR O CARTÃO NO CLIENTE
    // ==================================================================================
    public Mono<String> salvarCartaoNoCliente(String customerId, String token) {
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);

        return webClient.post()
                .uri("/v1/customers/" + customerId + "/cards")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(resp -> {
                    String cardId = (String) resp.get("id");
                    logger.info("Cartão salvo com sucesso. ID: {}", cardId);
                    return cardId;
                });
    }

    // ==================================================================================
    // ✅ PASSO 3: CRIAR ASSINATURA USANDO CARTÃO SALVO
    // (Usa card_id e payer_id em vez de token)
    // ==================================================================================
    public Mono<Map<String, Object>> criarAssinaturaComCartaoSalvo(
            Usuario usuario,
            String customerId,
            String cardId,
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

        // AQUI ESTÁ A MUDANÇA PRINCIPAL:
        body.put("payer_id", customerId); // Vincula ao cliente
        body.put("card_id", cardId);      // Usa o cartão salvo

        body.put("external_reference", usuario.getId().toString());
        body.put("back_url", this.baseUrl + "/pagamento-sucesso");

        // Com cartão salvo, podemos tentar autorizar direto.
        // Se der erro, o MP avisa.
        body.put("status", "authorized");

        return webClient.post()
                .uri("/preapproval")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // ==========================================================
    // MÉTODOS LEGADOS (Mantidos para compatibilidade se necessário)
    // ==========================================================
    public Mono<Map<String, Object>> criarPagamentoInicial(Usuario usuario, PaymentRequestDTO dto) { return Mono.empty(); }
    public Mono<Map<String, Object>> criarAssinatura(Usuario u, String t, String d, BigDecimal v) { return Mono.empty(); }


    // ==========================================================
    // 3. PIX (Igual)
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

        if(usuario.getCpf() != null) {
            payer.put("identification", Map.of("type", "CPF", "number", usuario.getCpf()));
        }
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