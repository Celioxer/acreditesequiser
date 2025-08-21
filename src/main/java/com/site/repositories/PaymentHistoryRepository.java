package com.site.repositories;

import com.site.models.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, String> {
    boolean existsByPaymentId(String paymentId);
}
