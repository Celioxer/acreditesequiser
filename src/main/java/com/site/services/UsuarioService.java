package com.site.services;

import com.site.models.Usuario;
import com.site.repositories.EmailLiberadoRepository;
import com.site.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmailLiberadoRepository emailLiberadoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, EmailLiberadoRepository emailLiberadoRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.emailLiberadoRepository = emailLiberadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // <<< MUDANÇA AQUI: Define o papel padrão para novos usuários
        // Isso garante que todo novo usuário comece com o acesso básico.
        usuario.setRole(Usuario.Role.USER);

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByEmail(username);
    }

    /**
     * Atualiza o status da assinatura do usuário.
     * Este método agora atualiza a data de validade E o papel do usuário para SUBSCRIBER.
     */
    @Transactional
    public void updateSubscriptionStatus(String email, int planDurationInDays) {
        // Busca o usuário pelo e-mail
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o email: " + email));

        // Lógica para calcular a nova data de validade
        LocalDateTime currentValidDate = usuario.getAcessoValidoAte();
        LocalDateTime newValidDate;

        if (currentValidDate == null || currentValidDate.isBefore(LocalDateTime.now())) {
            // Se não há assinatura ativa, começa a contar de agora
            newValidDate = LocalDateTime.now().plusDays(planDurationInDays);
        } else {
            // Se já há uma assinatura ativa, adiciona os dias ao final dela
            newValidDate = currentValidDate.plusDays(planDurationInDays);
        }

        usuario.setAcessoValidoAte(newValidDate);

        // <<< MUDANÇA AQUI: Promove o usuário para o papel de assinante.
        // É ESSENCIAL para que o WebSecurityConfig permita o acesso às áreas restritas.
        usuario.setRole(Usuario.Role.SUBSCRIBER);

        // Salva as alterações no banco de dados
        usuarioRepository.save(usuario);
    }

    /**
     * Verifica se o e-mail está na lista de acesso liberado (whitelist).
     * @param email O email a ser verificado.
     * @return true se o e-mail estiver na lista, false caso contrário.
     */
    public boolean isEmailLiberado(String email) {
        return emailLiberadoRepository.existsByEmail(email);
    }
}