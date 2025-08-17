package com.site.services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MercadoPagoService {

    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken) throws MPException {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public Payment createPixPayment(String emailDoCliente, String nomeDoCliente, String cpfDoCliente, String descricao, BigDecimal valor) throws MPException, MPApiException {
        // Crie o cliente da API de Pagamento
        PaymentClient client = new PaymentClient();

        // Crie o objeto de pagamento
        PaymentCreateRequest createRequest =
                PaymentCreateRequest.builder()
                        .transactionAmount(valor)
                        .description(descricao)
                        .paymentMethodId("pix")
                        .payer(
                                PaymentPayerRequest.builder()
                                        .email(emailDoCliente)
                                        .firstName(nomeDoCliente)
                                        .identification(IdentificationRequest.builder()
                                                .type("CPF")
                                                .number(cpfDoCliente)
                                                .build())
                                        .build())
                        .build();

        // Crie o pagamento na API do Mercado Pago
        Payment payment = client.create(createRequest);
        return payment;
    }
}