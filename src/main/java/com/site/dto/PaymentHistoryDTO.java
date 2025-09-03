package com.site.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentHistoryDTO {
    private String paymentId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentMethodId;
    private Integer installments;

    // Construtor, Getters e Setters
    public PaymentHistoryDTO(String paymentId, BigDecimal amount, LocalDateTime paymentDate, String paymentMethodId, Integer installments) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethodId = paymentMethodId;
        this.installments = installments;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getPaymentMethodId() { return paymentMethodId; }
    public Integer getInstallments() { return installments; }
}