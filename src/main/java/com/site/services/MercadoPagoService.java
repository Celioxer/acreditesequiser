package com.site.services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentItemRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.models.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class MercadoPagoService {

    private final String accessToken;
    private final PaymentClient paymentClient;
    private final String statementDescriptor = "ACREDITESQ"; // Nome que aparecerá na fatura

    // A inicialização agora acontece aqui, eliminando a necessidade do @PostConstruct
    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken) {
        this.accessToken = accessToken;
        MercadoPagoConfig.setAccessToken(this.accessToken); // Lógica de inicialização
        this.paymentClient = new PaymentClient();
    }

    // Métodos auxiliares para separar nome e sobrenome
    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts[0];
    }

    private String getLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    public Payment createPixPayment(Usuario usuario, String descricao, BigDecimal valor)
            throws MPException, MPApiException {

        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(usuario.getEmail())
                .firstName(getFirstName(usuario.getNome()))
                .lastName(getLastName(usuario.getNome()))
                .identification(IdentificationRequest.builder().type("CPF").number(usuario.getCpf()).build())
                .build();

        PaymentItemRequest item = PaymentItemRequest.builder()
                .id("APOIO_MENSAL")
                .title(descricao)
                .description("Acesso mensal para apoiadores do projeto")
                .categoryId("services")
                .quantity(1)
                .unitPrice(valor)
                .build();

        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(valor)
                .description(descricao)
                .paymentMethodId("pix")
                .payer(payer)
                .externalReference(usuario.getId().toString())
                .statementDescriptor(this.statementDescriptor)
                .additionalInfo(com.mercadopago.client.payment.PaymentAdditionalInfoRequest.builder()
                        .items(Collections.singletonList(item))
                        .build())
                .build();

        return paymentClient.create(request);
    }

    public Payment createCardPayment(Usuario usuario, String token, String descricao,
                                     BigDecimal valor, Integer installments, String paymentMethodId, Integer issuerId)
            throws MPException, MPApiException {

        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(usuario.getEmail())
                .firstName(getFirstName(usuario.getNome()))
                .lastName(getLastName(usuario.getNome()))
                .identification(IdentificationRequest.builder().type("CPF").number(usuario.getCpf()).build())
                .build();

        PaymentItemRequest item = PaymentItemRequest.builder()
                .id("APOIO_MENSAL")
                .title(descricao)
                .description("Acesso mensal para apoiadores do projeto")
                .categoryId("services")
                .quantity(1)
                .unitPrice(valor)
                .build();

        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(valor)
                .token(token)
                .description(descricao)
                .installments(installments)
                .paymentMethodId(paymentMethodId)
                .issuerId(issuerId != null ? String.valueOf(issuerId) : null)
                .payer(payer)
                .externalReference(usuario.getId().toString())
                .statementDescriptor(this.statementDescriptor)
                .additionalInfo(com.mercadopago.client.payment.PaymentAdditionalInfoRequest.builder()
                        .items(Collections.singletonList(item))
                        .build())
                .build();

        return paymentClient.create(request);
    }

    public Payment getPayment(String paymentId) throws MPException, MPApiException {
        return paymentClient.get(Long.parseLong(paymentId));
    }
}