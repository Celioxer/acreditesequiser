package com.site.services;

import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * NOVO MÉTODO: Busca um usuário por nome de usuário (e-mail).
     * Este método é necessário para que o SubscriptionController encontre
     * os dados do usuário logado e os use no pagamento.
     * @param username O nome de usuário (e-mail) do usuário a ser buscado.
     * @return Um Optional contendo o usuário, se encontrado, ou um Optional vazio.
     */
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByEmail(username);
    }
}
