package com.site.dto;

import java.math.BigDecimal;

// Esta classe representa o JSON enviado pelo seu formulário de pagamento
public class PaymentRequestDTO {
    private String token;
    private String paymentMethodId;
    private Integer installments;
    private BigDecimal valor;
    private String descricao;
    private PayerDTO payer;
    private Integer issuerId;

    // É importante ter getters e setters para o Spring conseguir preencher os dados
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public Integer getInstallments() { return installments; }
    public void setInstallments(Integer installments) { this.installments = installments; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public PayerDTO getPayer() { return payer; }
    public void setPayer(PayerDTO payer) { this.payer = payer; }

    public static class PayerDTO {
        private String email;
        private IdentificationDTO identification;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public IdentificationDTO getIdentification() { return identification; }
        public void setIdentification(IdentificationDTO identification) { this.identification = identification; }
    }

    public static class IdentificationDTO {
        private String type;
        private String number;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
    }
    public Integer getIssuerId() { return issuerId; }
    public void setIssuerId(Integer issuerId) { this.issuerId = issuerId; }
}
