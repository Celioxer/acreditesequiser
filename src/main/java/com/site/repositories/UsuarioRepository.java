package com.site.repositories;

import com.site.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.site.models.PaymentHistory;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
