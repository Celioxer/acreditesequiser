package com.site.security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.services.MercadoPagoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MercadoPagoService mercadoPagoService;
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
    public CustomAuthenticationSuccessHandler(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Obtenha o nome de usuário autenticado
        String username = authentication.getName();

        // --- AQUI VAI A LÓGICA QUE ESTAVA NO SEU CONTROLLER ---
        // Você precisará buscar os dados do usuário a partir do username,
        // pois a autenticação do Spring Security não fornece o objeto Usuario completo.
        // Por enquanto, vamos simular os dados
        String email = "celioxer@gmail.com";
        String nome = "Célio Ribeiro da Silva";
        String cpf = "04702327909";

        try {
            Payment payment = mercadoPagoService.createPixPayment(
                    email,
                    nome,
                    cpf,
                    "Descrição do seu produto/serviço",
                    new BigDecimal("2.00")
            );

            String pixUrl = payment.getPointOfInteraction().getTransactionData().getTicketUrl();
            logger.info("URL do PIX gerada: {}", pixUrl);
            response.sendRedirect(pixUrl);


        } catch (MPException | MPApiException e) {
            e.printStackTrace();
            // Em caso de erro, redireciona para a página de login com uma mensagem de erro
            response.sendRedirect("/login?error=payment_error");
        }
    }
}