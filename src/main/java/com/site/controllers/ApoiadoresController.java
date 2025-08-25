package com.site.controllers;

import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApoiadoresController {

    private final UsuarioRepository usuarioRepository;

    public ApoiadoresController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/apoiadores")
    public String showApoiadoresPage(Authentication authentication, Model model) {
        // 1. Pega o email do usuário que está logado
        String email = authentication.getName();

        // 2. Busca o usuário completo no banco de dados
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado no banco de dados."));

        // 3. Adiciona o objeto 'usuario' completo ao modelo,
        // tornando-o acessível na página HTML
        model.addAttribute("usuario", usuario);

        // 4. Retorna o nome do arquivo HTML a ser renderizado
        return "apoiadores"; // Deve corresponder a /templates/apoiadores.html
    }
}