package com.site.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // <<< LÓGICA ANTIGA REMOVIDA
        // A nova abordagem não precisa de uma lista de e-mails ou de verificar a data aqui,
        // pois o Spring Security já sabe os papéis (Roles) do usuário logado.

        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

    protected void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    /**
     * <<< NOVA LÓGICA BASEADA EM ROLES
     * Decide a URL de destino com base nos papéis (Roles) do usuário.
     */
    protected String determineTargetUrl(final Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Set<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // SE o usuário for ADMIN ou SUBSCRIBER (Assinante)...
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_SUBSCRIBER")) {
            return "/apoiadores"; // ...redireciona para a página de apoiadores.
        }
        // SE o usuário for apenas um USER comum (não pagou)...
        else if (roles.contains("ROLE_USER")) {
            return "/subscription"; // ...redireciona para a página de pagamento/assinatura.
        }
        // Caso contrário, volta para a página de login com erro.
        else {
            return "/login?error=true";
        }
    }

    /**
     * Limpa atributos temporários da sessão após o login.
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}