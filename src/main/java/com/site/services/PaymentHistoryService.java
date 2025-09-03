package com.site.services;

import com.site.models.PaymentHistory;
import com.site.repositories.PaymentHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository) {
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    public boolean existsByPaymentId(String paymentId) {
        return paymentHistoryRepository.existsByPaymentId(paymentId);
    }

    // ****** MÉTODO ATUALIZADO PARA RECEBER TODAS AS NOVAS INFORMAÇÕES ******
    public void savePayment(String paymentId, Long usuarioId, BigDecimal amount, LocalDateTime paymentDate,
                            String paymentMethodId, String status, String statusDetail, Integer installments) {

        PaymentHistory history = new PaymentHistory(paymentId, usuarioId, amount, paymentDate,
                paymentMethodId, status, statusDetail, installments);
        paymentHistoryRepository.save(history);
    }
}