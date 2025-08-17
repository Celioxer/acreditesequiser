package com.site.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.services.MercadoPagoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Objects;

@Controller
public class SubscriptionController {

    private final MercadoPagoService mercadoPagoService;

    public SubscriptionController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @GetMapping("/subscription")
    public String showSubscriptionPage() {
        return "auth/subscription";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam("plan") String plan, Authentication authentication, RedirectAttributes redirectAttributes) {

        // Simule os dados do usuário autenticado (você deve obter isso de alguma forma)
        String username = authentication.getName();
        String email = "usuario@teste.com";
        String nome = "Usuário Teste";
        String cpf = "12345678909";

        BigDecimal valor;
        String descricao;

        if (Objects.equals(plan, "basic")) {
            valor = new BigDecimal("30.00");
            descricao = "Plano Básico - 30 dias de acesso";
        } else if (Objects.equals(plan, "premium")) {
            valor = new BigDecimal("60.00");
            descricao = "Plano Premium - 30 dias + Conteúdo Exclusivo";
        } else {
            // Plano inválido
            redirectAttributes.addFlashAttribute("error", "Plano de assinatura inválido.");
            return "redirect:/subscription";
        }

        try {
            Payment payment = mercadoPagoService.createPixPayment(
                    email,
                    nome,
                    cpf,
                    descricao,
                    valor
            );

            String pixUrl = payment.getPointOfInteraction().getTransactionData().getTicketUrl();
            return "redirect:" + pixUrl;

        } catch (MPException | MPApiException e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar pagamento: " + e.getMessage());
            return "redirect:/subscription";
        }
    }
}