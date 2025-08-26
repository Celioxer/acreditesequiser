package com.site.services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.models.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;

@Service
public class MercadoPagoService {

    //@Value("${mercadopago.access.token}")
    private final String accessToken;
    private final PaymentClient paymentClient;


    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken) {
        this.accessToken = accessToken;
        MercadoPagoConfig.setAccessToken(this.accessToken); // Inicializa aqui
        this.paymentClient = new PaymentClient();
    }

    /**
     * Cria um pagamento Pix para um usuário.
     * MÉTODO MANTIDO - JÁ ESTAVA CORRETO.
     */
    public Payment createPixPayment(Usuario usuario, String descricao, BigDecimal valor)
            throws MPException, MPApiException {

        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(usuario.getEmail())
                .firstName(usuario.getNome())
                .identification(IdentificationRequest.builder()
                        .type("CPF")
                        .number(usuario.getCpf())
                        .build())
                .build();

        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(valor)
                .description(descricao)
                .paymentMethodId("pix")
                .payer(payer)
                .externalReference(usuario.getId().toString()) // CORRETO!
                .build();

        return paymentClient.create(request);
    }

    /**
     * MÉTODO DE CARTÃO CORRIGIDO E UNIFICADO.
     * Agora recebe o objeto 'Usuario' para podermos obter o ID.
     */
    public Payment createCardPayment(Usuario usuario, String token, String descricao,
                                     BigDecimal valor, Integer installments, String paymentMethodId)
            throws MPException, MPApiException {

        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(usuario.getEmail())
                .firstName(usuario.getNome())
                .identification(IdentificationRequest.builder()
                        .type("CPF")
                        .number(usuario.getCpf())
                        .build())
                .build();

        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(valor)
                .token(token)
                .description(descricao)
                .installments(installments)
                .paymentMethodId(paymentMethodId)
                .payer(payer)
                // *** A CORREÇÃO ESSENCIAL ESTÁ AQUI ***
                .externalReference(usuario.getId().toString())
                .build();

        return paymentClient.create(request);
    }

    /**
     * Obtém um pagamento do Mercado Pago pelo seu ID.
     */
    public Payment getPayment(String paymentId) throws MPException, MPApiException {
        // O SDK espera um Long, então a conversão está correta.
        return paymentClient.get(Long.parseLong(paymentId));
    }
}