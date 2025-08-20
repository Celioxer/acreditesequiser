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
import java.time.LocalDateTime;

@Service
public class MercadoPagoService {

    private final String accessToken;

    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken) {
        this.accessToken = accessToken;
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public Payment createPixPayment(String email, String nome, String cpf, String descricao, BigDecimal valor)
            throws MPException, MPApiException {

        PaymentClient client = new PaymentClient();

        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(email)
                .firstName(nome)
                .identification(IdentificationRequest.builder()
                        .type("CPF")
                        .number(cpf)
                        .build())
                .build();

        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(valor)
                .description(descricao)
                .paymentMethodId("pix")
                .payer(payer)
                .build();

        return client.create(createRequest);
    }

    /**
     * NOVO MÉTODO: Cria um pagamento com cartão de crédito usando um token.
     * @param token O token do cartão, gerado no frontend pelo Mercado Pago SDK.
     * @param email O email do pagador.
     * @param nome O nome do pagador.
     * @param cpf O CPF do pagador.
     * @param descricao A descrição do pagamento.
     * @param valor O valor do pagamento.
     * @return O objeto de pagamento criado.
     * @throws MPException Em caso de erro de comunicação com a API.
     * @throws MPApiException Em caso de erro na API do Mercado Pago.
     */
    public Payment createCardPayment(String token, String email, String nome, String cpf, String descricao, BigDecimal valor)
            throws MPException, MPApiException {

        PaymentClient client = new PaymentClient();

        // Constrói o objeto do pagador
        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(email)
                .firstName(nome)
                .identification(IdentificationRequest.builder()
                        .type("CPF")
                        .number(cpf)
                        .build())
                .build();

        // Constrói a requisição de criação de pagamento com cartão
        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(valor)
                .description(descricao)
                .token(token) // Utiliza o token gerado no frontend
                .installments(1) // Por padrão, 1 parcela. Você pode tornar isso dinâmico.
                .payer(payer)
                .build();

        // Cria o pagamento na API do Mercado Pago
        return client.create(createRequest);
    }

    /**
     * NOVO MÉTODO: Obtém um pagamento do Mercado Pago pelo seu ID.
     * Este método é crucial para o webhook.
     * @param paymentId O ID do pagamento a ser buscado.
     * @return O objeto Payment com os detalhes do pagamento.
     * @throws MPException Em caso de erro de conexão ou configuração.
     * @throws MPApiException Em caso de erro na API do Mercado Pago.
     */
    public Payment getPayment(String paymentId) throws MPException, MPApiException {
        PaymentClient client = new PaymentClient();
        return client.get(Long.parseLong(paymentId));
    }
}
