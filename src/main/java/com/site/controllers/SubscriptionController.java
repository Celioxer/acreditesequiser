package com.site.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.services.MercadoPagoService;
import com.site.models.Usuario; // O modelo de usuário agora é "Usuario"
import com.site.services.UsuarioService; // O serviço de usuário agora é "UsuarioService"
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
public class SubscriptionController {

    private final MercadoPagoService mercadoPagoService;
    private final UsuarioService usuarioService; // Injeção de dependência do serviço de usuário

    public SubscriptionController(MercadoPagoService mercadoPagoService, UsuarioService usuarioService) {
        this.mercadoPagoService = mercadoPagoService;
        this.usuarioService = usuarioService;
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

        // --- ALTERAÇÃO PRINCIPAL PARA PRODUÇÃO ---
        // Obtenha os dados do usuário do banco de dados de forma dinâmica e segura.
        // O `authentication.getName()` geralmente retorna o username/email do usuário logado.
        String username = authentication.getName();
        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(username);

        if (!optionalUsuario.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Erro: Usuário não encontrado.");
            return "redirect:/subscription";
        }

        Usuario usuario = optionalUsuario.get();
        String email = usuario.getEmail();
        String nome = usuario.getNome();
        String cpf = usuario.getCpf(); // Supondo que o CPF está no modelo de usuário

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
                // Agora, o método `createPixPayment` usará as credenciais de produção
                // e os dados do usuário real.
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
    public ResponseEntity<?> processCardPayment(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {

        String token = payload.get("token");
        String plan = payload.get("plan");

        // Obtenha os dados do usuário do banco de dados de forma dinâmica e segura.
        String username = authentication.getName();
        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(username);

        if (!optionalUsuario.isPresent()) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Erro: Usuário não encontrado."));
        }

        Usuario usuario = optionalUsuario.get();
        String email = usuario.getEmail();
        String nome = usuario.getNome();
        String cpf = usuario.getCpf();

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
            // O método `createCardPayment` usará as credenciais de produção
            // e os dados do usuário real.
            Payment payment = mercadoPagoService.createCardPayment(token, email, nome, cpf, descricao, valor);

            if ("approved".equals(payment.getStatus())) {
                // Em vez de redirecionar, retorne um JSON com a URL de sucesso
                return ResponseEntity.ok(Collections.singletonMap("redirectUrl", "/payment_success"));
            } else {
                return ResponseEntity.ok(Collections.singletonMap("redirectUrl", "/subscription"));
            }
        } catch (MPException | MPApiException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Erro ao processar pagamento: " + e.getMessage()));
        }

    }

}
