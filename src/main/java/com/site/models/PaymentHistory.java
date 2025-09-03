package com.site.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    private String paymentId;

    private Long usuarioId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    // ****** NOVOS CAMPOS ÚTEIS ADICIONADOS ******
    @Column(nullable = false)
    private String paymentMethodId; // Ex: "pix", "visa", "master"

    @Column(nullable = false)
    private String status; // Ex: "approved"

    private String statusDetail; // Ex: "accredited"

    private Integer installments; // Número de parcelas (para cartões)
    // ********************************************

    // Construtor padrão (necessário para JPA)
    public PaymentHistory() {}

    // Construtor atualizado para receber todos os novos dados
    public PaymentHistory(String paymentId, Long usuarioId, BigDecimal amount, LocalDateTime paymentDate,
                          String paymentMethodId, String status, String statusDetail, Integer installments) {
        this.paymentId = paymentId;
        this.usuarioId = usuarioId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethodId = paymentMethodId;
        this.status = status;
        this.statusDetail = statusDetail;
        this.installments = installments;
    }

    // --- Getters e Setters ---

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    // ****** GETTERS E SETTERS PARA OS NOVOS CAMPOS ******
    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusDetail() { return statusDetail; }
    public void setStatusDetail(String statusDetail) { this.statusDetail = statusDetail; }

    public Integer getInstallments() { return installments; }
    public void setInstallments(Integer installments) { this.installments = installments; }
    // ****************************************************
}