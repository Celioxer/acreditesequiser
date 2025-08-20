package com.site.services;

import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca um usuário por nome de usuário (e-mail).
     * @param username O nome de usuário (e-mail) do usuário.
     * @return Um Optional contendo o usuário, se encontrado.
     */
    public Optional<Usuario> findByUsername(String username) {
        // Assume que o email é o "username" para a busca.
        return usuarioRepository.findByEmail(username);
    }

    /**
     * Atualiza o status da assinatura de um usuário após um pagamento aprovado.
     * @param email O email do usuário cuja assinatura será atualizada.
     * @param planDurationInDays A duração do plano em dias.
     */
    @Transactional
    public void updateSubscriptionStatus(String email, int planDurationInDays) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            LocalDateTime currentValidDate = usuario.getAcessoValidoAte();
            LocalDateTime newValidDate;

            if (currentValidDate == null || currentValidDate.isBefore(LocalDateTime.now())) {
                // Se o acesso expirou, comece a contar a partir de agora
                newValidDate = LocalDateTime.now().plusDays(planDurationInDays);
            } else {
                // Se o acesso ainda é válido, adicione dias a partir da data de validade atual
                newValidDate = currentValidDate.plusDays(planDurationInDays);
            }
            usuario.setAcessoValidoAte(newValidDate);
            usuarioRepository.save(usuario);
        }
    }
}
