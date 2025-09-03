package com.site.repositories;

import com.site.models.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, String> {

    boolean existsByPaymentId(String paymentId);

    long countByUsuarioId(Long usuarioId);

    Optional<PaymentHistory> findTopByUsuarioIdOrderByPaymentDateDesc(Long usuarioId);


    List<PaymentHistory> findByUsuarioIdOrderByPaymentDateDesc(Long usuarioId);
}