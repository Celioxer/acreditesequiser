package com.site.security;

import com.site.models.Usuario;
import com.site.services.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // Lista de e-mails que podem acessar sem precisar pagar.
    private static final List<String> ACESSO_LIBERADO = Arrays.asList(
            "celioxer@gmail.com",
            "phcarvalho76@gmail.com"
    ).stream().map(String::toLowerCase).collect(Collectors.toList());

    private final UsuarioService usuarioService;

    public CustomAuthenticationSuccessHandler(@Lazy UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName().toLowerCase();
        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(username);

        if (!optionalUsuario.isPresent()) {
            response.sendRedirect("/login?error=userNotFound");
            return;
        }

        Usuario usuario = optionalUsuario.get();

        // Verifica se o usuário tem acesso liberado OU se a assinatura está válida.
        boolean isAcessoLiberado = ACESSO_LIBERADO.contains(username);
        boolean isAssinaturaValida = usuario.getAcessoValidoAte() != null && usuario.getAcessoValidoAte().isAfter(LocalDateTime.now());

        if (isAcessoLiberado || isAssinaturaValida) {
            // A ÚNICA MUDANÇA É AQUI:
            response.sendRedirect("/apoiadores"); // Redireciona para a nova página de boas-vindas
        } else {
            // Esta lógica continua igual: se não tiver acesso, vai para a página de assinatura
            response.sendRedirect("/subscription");
        }
    }
}