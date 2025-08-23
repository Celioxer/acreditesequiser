package com.site.services;

import com.site.models.EmailLiberado;
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
    private final EmailLiberadoRepository emailLiberadoRepository; // Injeta o novo repositório
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
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByEmail(username);
    }

    @Transactional
    public void updateSubscriptionStatus(String email, int planDurationInDays) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            LocalDateTime currentValidDate = usuario.getAcessoValidoAte();
            LocalDateTime newValidDate;

            if (currentValidDate == null || currentValidDate.isBefore(LocalDateTime.now())) {
                newValidDate = LocalDateTime.now().plusDays(planDurationInDays);
            } else {
                newValidDate = currentValidDate.plusDays(planDurationInDays);
            }

            usuario.setAcessoValidoAte(newValidDate);
            usuarioRepository.save(usuario);
        }
    }

    /**
     * NOVO MÉTODO: Verifica se o e-mail está na lista de acesso liberado.
     * @param email O email a ser verificado.
     * @return true se o e-mail estiver na lista, false caso contrário.
     */
    public boolean isEmailLiberado(String email) {
        return emailLiberadoRepository.existsByEmail(email);
    }
}
