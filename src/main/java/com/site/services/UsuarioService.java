package com.site.services;

import com.site.models.Usuario;
import com.site.repositories.EmailLiberadoRepository;
import com.site.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmailLiberadoRepository emailLiberadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // <<< INJEÇÃO NOVA

    // Construtor atualizado para incluir o EmailService
    public UsuarioService(UsuarioRepository usuarioRepository, EmailLiberadoRepository emailLiberadoRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.emailLiberadoRepository = emailLiberadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService; // <<< INJEÇÃO NOVA
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setRole(Usuario.Role.USER);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByEmail(username);
    }

    @Transactional
    public void updateSubscriptionStatus(String email, int planDurationInDays) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o email: " + email));

        LocalDateTime currentValidDate = usuario.getAcessoValidoAte();
        LocalDateTime newValidDate;

        if (currentValidDate == null || currentValidDate.isBefore(LocalDateTime.now())) {
            newValidDate = LocalDateTime.now().plusDays(planDurationInDays);
        } else {
            newValidDate = currentValidDate.plusDays(planDurationInDays);
        }

        usuario.setAcessoValidoAte(newValidDate);
        usuario.setRole(Usuario.Role.SUBSCRIBER);
        usuarioRepository.save(usuario);
    }

    public boolean isEmailLiberado(String email) {
        return emailLiberadoRepository.existsByEmail(email);
    }

    // =================================================================
    // <<< MÉTODOS NOVOS PARA REDEFINIÇÃO DE SENHA >>>
    // =================================================================

    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o email: " + email));

        String token = UUID.randomUUID().toString();
        usuario.setPasswordResetToken(token);
        usuario.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(30)); // Token expira em 30 minutos
        usuarioRepository.save(usuario);

        emailService.sendPasswordResetEmail(usuario.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Você precisará adicionar 'findByPasswordResetToken' ao seu UsuarioRepository
        Usuario usuario = usuarioRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token de redefinição inválido."));

        if (usuario.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de redefinição expirado.");
        }

        usuario.setSenha(passwordEncoder.encode(newPassword));
        // Limpa o token para que não possa ser usado novamente
        usuario.setPasswordResetToken(null);
        usuario.setPasswordResetTokenExpiry(null);
        usuarioRepository.save(usuario);
    }
}