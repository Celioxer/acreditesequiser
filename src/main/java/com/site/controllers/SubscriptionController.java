package com.site.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.models.Usuario;
import com.site.services.MercadoPagoService;
import com.site.services.UsuarioService;
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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
public class SubscriptionController {

    private final MercadoPagoService mercadoPagoService;
    private final UsuarioService usuarioService;

    public SubscriptionController(MercadoPagoService mercadoPagoService, UsuarioService usuarioService) {
        this.mercadoPagoService = mercadoPagoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/subscription")
    public String showSubscriptionPage() {
        return "auth/subscription";
    }

    /**
     * Processa o checkout com valor e método de pagamento dinâmicos.
     */
    @GetMapping("/checkout")
    public String checkout(
            @RequestParam("valor") BigDecimal valor,
            @RequestParam("paymentMethod") String paymentMethod,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validação do valor mínimo no backend (importante para segurança)
        if (valor.compareTo(new BigDecimal("10.00")) < 0) {
            redirectAttributes.addFlashAttribute("error", "O valor do apoio deve ser de no mínimo R$ 10,00.");
            return "redirect:/subscription";
        }

        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(authentication.getName());
        if (optionalUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Erro: Usuário não encontrado.");
            return "redirect:/subscription";
        }
        Usuario usuario = optionalUsuario.get();

        String descricao = "Apoio Mensal - Acesso Apoiador";

        if ("pix".equals(paymentMethod)) {
            try {
                Payment payment = mercadoPagoService.createPixPayment(usuario, descricao, valor);
                String pixUrl = payment.getPointOfInteraction().getTransactionData().getTicketUrl();
                return "redirect:" + pixUrl;
            } catch (MPException | MPApiException e) {
                redirectAttributes.addFlashAttribute("error", "Erro ao criar pagamento Pix: " + e.getMessage());
                return "redirect:/subscription";
            }
        } else if ("card".equals(paymentMethod)) {
            model.addAttribute("valor", valor);
            model.addAttribute("descricao", descricao);
            return "auth/card_payment";
        } else {
            redirectAttributes.addFlashAttribute("error", "Método de pagamento inválido.");
            return "redirect:/subscription";
        }
    }

    /**
     * Lida com a requisição POST do formulário de cartão.
     */
    @PostMapping("/process-card-payment")
    public ResponseEntity<?> processCardPayment(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {

        String token = payload.get("token");
        BigDecimal valor = new BigDecimal(payload.get("valor"));
        String descricao = payload.get("descricao");

        // Validação do valor mínimo no backend
        if (valor.compareTo(new BigDecimal("10.00")) < 0) {
            return ResponseEntity.status(400).body(Map.of("error", "O valor do apoio deve ser de no mínimo R$ 10,00."));
        }

        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(authentication.getName());
        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro: Usuário não encontrado."));
        }
        Usuario usuario = optionalUsuario.get();

        int planoDuracaoDias = 30; // Sempre 30 dias de acesso

        try {
            Payment payment = mercadoPagoService.createCardPayment(token, usuario.getEmail(), usuario.getNome(), usuario.getCpf(), descricao, valor);

            if ("approved".equals(payment.getStatus())) {
                usuarioService.updateSubscriptionStatus(usuario.getEmail(), planoDuracaoDias);
                return ResponseEntity.ok(Map.of("redirectUrl", "/apoiadores"));
            } else {
                return ResponseEntity.ok(Map.of("redirectUrl", "/subscription?error=payment_refused"));
            }
        } catch (MPException | MPApiException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao processar pagamento: " + e.getMessage()));
        }
    }

    @GetMapping("/conteudo-protegido")
    public String getProtectedContent(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(username);

        if (optionalUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Erro: Usuário não encontrado.");
            return "redirect:/subscription";
        }

        Usuario usuario = optionalUsuario.get();

        if ((usuario.getAcessoValidoAte() != null && usuario.getAcessoValidoAte().isAfter(LocalDateTime.now()))) {
            model.addAttribute("mensagem", "Bem-vindo! Você tem acesso ao conteúdo protegido.");
            return "protected-content-page";
        } else {
            redirectAttributes.addFlashAttribute("error", "Assinatura necessária para acessar este conteúdo.");
            return "redirect:/subscription";
        }
    }

    @GetMapping("/episodios")
    public String showEpisodesPage() {
        return "episodios";
    }
}