package com.site.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.services.MercadoPagoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Controller
public class SubscriptionController {

    private final MercadoPagoService mercadoPagoService;

    public SubscriptionController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    /**
     * Exibe a página de seleção de planos. Este é o destino
     * inicial após o login.
     */
    @GetMapping("/subscription")
    public String showSubscriptionPage() {
        return "auth/subscription";
    }

    /**
     * Processa a seleção do plano e método de pagamento.
     * Este método é chamado a partir dos botões na página /subscription.
     */
    @GetMapping("/checkout")
    public String checkout(
            @RequestParam("plan") String plan,
            @RequestParam("paymentMethod") String paymentMethod,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Simule os dados do usuário autenticado
        // Em um cenário real, você obteria esses dados do banco de dados
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
            redirectAttributes.addFlashAttribute("error", "Plano de assinatura inválido.");
            return "redirect:/subscription";
        }

        if ("pix".equals(paymentMethod)) {
            try {
                Payment payment = mercadoPagoService.createPixPayment(
                        email, nome, cpf, descricao, valor
                );
                String pixUrl = payment.getPointOfInteraction().getTransactionData().getTicketUrl();
                return "redirect:" + pixUrl;
            } catch (MPException | MPApiException e) {
                redirectAttributes.addFlashAttribute("error", "Erro ao criar pagamento Pix: " + e.getMessage());
                return "redirect:/subscription";
            }
        } else if ("card".equals(paymentMethod)) {
            // Se o método for cartão, passe os dados para a página do formulário
            // É crucial passar o 'plan' também para que o frontend o inclua na requisição POST
            model.addAttribute("valor", valor);
            model.addAttribute("descricao", descricao);
            model.addAttribute("plan", plan);
            return "auth/card_payment";
        } else {
            redirectAttributes.addFlashAttribute("error", "Método de pagamento inválido.");
            return "redirect:/subscription";
        }
    }

    /**
     * NOVO MÉTODO: Lida com a requisição POST do formulário de cartão.
     */
    @PostMapping("/process-card-payment")
    public String processCardPayment(
            @RequestBody Map<String, String> payload,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Obtenha os dados do payload JSON
        String token = payload.get("token");
        String plan = payload.get("plan");

        // Simule os dados do usuário autenticado (deve ser obtido do `authentication.getName()`)
        String email = "usuario@teste.com";
        String nome = "Usuário Teste";
        String cpf = "12345678909";

        BigDecimal valor;
        String descricao;

        if (Objects.equals(plan, "basic")) {
            valor = new BigDecimal("30.00");
            descricao = "Plano Básico - 30 dias de acesso";
        } else {
            valor = new BigDecimal("60.00");
            descricao = "Plano Premium - 30 dias + Conteúdo Exclusivo";
        }

        try {
            // Este método precisa ser implementado no MercadoPagoService
            // Ele irá criar um pagamento com o token do cartão
            Payment payment = mercadoPagoService.createCardPayment(token, email, nome, cpf, descricao, valor);

            // Verifique o status do pagamento
            if ("approved".equals(payment.getStatus())) {
                return "redirect:/payment_success";
            } else {
                redirectAttributes.addFlashAttribute("error", "Pagamento não aprovado. Status: " + payment.getStatus());
                return "redirect:/subscription";
            }

        } catch (MPException | MPApiException e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao processar pagamento: " + e.getMessage());
            return "redirect:/subscription";
        }
    }
}
