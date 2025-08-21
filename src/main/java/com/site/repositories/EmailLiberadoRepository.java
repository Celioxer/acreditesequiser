package com.site.repositories;

import com.site.models.EmailLiberado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailLiberadoRepository extends JpaRepository<EmailLiberado, Long> {
    Optional<EmailLiberado> findByEmail(String email);
    boolean existsByEmail(String email);
}
