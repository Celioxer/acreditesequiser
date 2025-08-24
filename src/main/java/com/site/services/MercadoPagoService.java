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
        // Assegura que o token de acesso seja configurado
        MercadoPagoConfig.setAccessToken(accessToken);
        this.paymentClient = new PaymentClient();
    }

    /**
     * Cria um pagamento Pix para um usuário.
     * @param usuario O objeto de usuário com os dados do pagador.
     * @param descricao A descrição do pagamento.
     * @param valor O valor do pagamento.
     * @return O objeto de pagamento criado.
     * @throws MPException Em caso de erro de comunicação com a API.
     * @throws MPApiException Em caso de erro na API do Mercado Pago.
     */
    public Payment createPixPayment(Usuario usuario, String descricao, BigDecimal valor)
            throws MPException, MPApiException {

        // 🔹 Primeiro monta o payer com todos os dados necessários
        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(usuario.getEmail())
                .firstName(usuario.getNome())
                .identification(IdentificationRequest.builder()
                        .type("CPF")
                        .number(usuario.getCpf())
                        .build())
                .build();

        // 🔹 Depois monta a request
        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(valor)
                .description(descricao)
                .paymentMethodId("pix")
                .payer(payer)
                .externalReference(usuario.getId().toString()) // Liga ao usuário
                .build();

        return paymentClient.create(request);
    }

    /**
     * Cria um pagamento com cartão de crédito usando um token.
     * @param token O token do cartão.
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
                .token(token)
                .installments(1)
                .payer(payer)
                .build();

        return client.create(createRequest);
    }

    /**
     * Obtém um pagamento do Mercado Pago pelo seu ID.
     * @param paymentId O ID do pagamento a ser buscado.
     * @return O objeto Payment com os detalhes do pagamento.
     * @throws MPException Em caso de erro de conexão ou configuração.
     * @throws MPApiException Em caso de erro na API do Mercado Pago.
     */
    public Payment getPayment(String paymentId) throws MPException, MPApiException {
        return paymentClient.get(Long.valueOf(paymentId));
    }

}
