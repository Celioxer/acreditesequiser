package com.site.models;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    private String paymentId; // ID único do pagamento no Mercado Pago

    @Column(nullable = false)
    private Long usuarioId;

    public PaymentHistory() {}

    public PaymentHistory(String paymentId, Long usuarioId) {
        this.paymentId = paymentId;
        this.usuarioId = usuarioId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
