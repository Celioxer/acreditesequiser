package com.site.repositories;

import com.site.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    // Conta usuários que são SUBSCRIBER e cuja data de acesso é posterior a agora.
    long countByRoleAndAcessoValidoAteAfter(Usuario.Role role, LocalDateTime dataAtual);

    // Encontra todos os usuários que são SUBSCRIBER e cuja assinatura está ativa.
    List<Usuario> findByRoleAndAcessoValidoAteAfter(Usuario.Role role, LocalDateTime dataAtual);

    // <<< MÉTODO NOVO PARA A REDEFINIÇÃO DE SENHA >>>
    Optional<Usuario> findByPasswordResetToken(String token);
    // <<< Serviço de email para usuarios vencidos >>>
    List<Usuario> findByAcessoValidoAteBetween(LocalDateTime start, LocalDateTime end);
}