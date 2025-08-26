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

    private final PaymentClient paymentClient;

    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken) {
        MercadoPagoConfig.setAccessToken(accessToken);
        this.paymentClient = new PaymentClient();
    }

    /**
     * Cria um pagamento Pix para um usuário.
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
                .externalReference(usuario.getId().toString())
                .build();

        return paymentClient.create(request);
    }

    /**
     * Cria um pagamento com cartão de crédito usando um token.
     */
    public Payment createCardPayment(String token, String email, String nome, String cpf,
                                     String descricao, BigDecimal valor, Integer installments,
                                     String paymentMethodId, Long issuerId)
            throws MPException, MPApiException {

        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(email)
                .firstName(nome)
                .identification(IdentificationRequest.builder()
                        .type("CPF")
                        .number(cpf)
                        .build())
                .build();

        // CORREÇÃO: Criar o builder e configurar tudo de uma vez
        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(valor)
                .description(descricao)
                .token(token)
                .paymentMethodId(paymentMethodId)
                .installments(installments)
                .payer(payer)
                .build();

        // Se precisar adicionar issuerId, você pode precisar usar um método diferente
        // dependendo da versão do SDK
        return paymentClient.create(request);
    }

    /**
     * Versão alternativa sem issuerId
     */
    public Payment createCardPayment(String token, String email, String nome, String cpf,
                                     String descricao, BigDecimal valor, Integer installments,
                                     String paymentMethodId)
            throws MPException, MPApiException {
        return createCardPayment(token, email, nome, cpf, descricao, valor, installments, paymentMethodId, null);
    }

    /**
     * Obtém um pagamento do Mercado Pago pelo seu ID.
     */
    public Payment getPayment(String paymentId) throws MPException, MPApiException {
        return paymentClient.get(Long.valueOf(paymentId));
    }
}