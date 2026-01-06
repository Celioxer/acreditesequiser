package com.site.services;

import com.site.models.Usuario;
import com.site.repositories.EmailLiberadoRepository;
import com.site.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmailLiberadoRepository emailLiberadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            EmailLiberadoRepository emailLiberadoRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.emailLiberadoRepository = emailLiberadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // ==========================================================
    // ✅ MÉTODO ADICIONADO PARA O WEBHOOK
    // ==========================================================
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    // ==========================================================
    // ✅ USADO PELOS WEBHOOKS DO MERCADO PAGO
    // ==========================================================

    @Transactional
    public void liberarAssinatura(Long usuarioId, int dias) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime atual = usuario.getAcessoValidoAte();

        // Esta lógica de "empilhar" está PERFEITA.
        LocalDateTime novoVencimento =
                (atual == null || atual.isBefore(agora))
                        ? agora.plusDays(dias)      // pagamento novo
                        : atual.plusDays(dias);      // renovação antecipada

        usuario.setAcessoValidoAte(novoVencimento);
        usuario.setRole(Usuario.Role.SUBSCRIBER); // Define como assinante

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void removerAssinatura(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));

        usuario.setRole(Usuario.Role.USER);
        usuario.setAcessoValidoAte(LocalDateTime.now().minusMinutes(1)); // Expira o acesso

        usuarioRepository.save(usuario);
    }

    // ==========================================================
    // ✅ SISTEMA DE EXPIRAÇÃO AUTOMÁTICA
    // ==========================================================

    @Transactional
    public long desativarAssinaturasVencidas() {
        List<Usuario> expiram = usuarioRepository
                .findByRoleAndAcessoValidoAteBefore(Usuario.Role.SUBSCRIBER, LocalDateTime.now());

        expiram.forEach(u -> u.setRole(Usuario.Role.USER));
        usuarioRepository.saveAll(expiram);

        return expiram.size();
    }

    // ==========================================================
    // ✅ Registro básico de usuários
    // ==========================================================

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByEmail(username);
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

    // ==========================================================
    // ✅ Redefinição de senha
    // ==========================================================

    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = UUID.randomUUID().toString();
        usuario.setPasswordResetToken(token);
        usuario.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(30));

        usuarioRepository.save(usuario);

        emailService.sendPasswordResetEmail(usuario.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Usuario usuario = usuarioRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido."));

        if (usuario.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado.");
        }

        usuario.setSenha(passwordEncoder.encode(newPassword));
        usuario.setPasswordResetToken(null);
        usuario.setPasswordResetTokenExpiry(null);

        usuarioRepository.save(usuario);
    }
}
