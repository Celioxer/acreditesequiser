package com.site.services;

import com.site.models.PaymentHistory;
import com.site.repositories.PaymentHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository) {
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    public boolean existsByPaymentId(String paymentId) {
        return paymentHistoryRepository.existsByPaymentId(paymentId);
    }

    public void savePayment(String paymentId, Long usuarioId) {
        PaymentHistory history = new PaymentHistory(paymentId, usuarioId);
        paymentHistoryRepository.save(history);
    }
}
