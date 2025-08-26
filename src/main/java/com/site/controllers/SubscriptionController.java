package com.site.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.dto.PaymentRequestDTO;
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
import org.springframework.web.bind.annotation.ResponseBody; // <-- Importação necessária
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

    @GetMapping("/checkout")
    public String checkout(
            @RequestParam("valor") BigDecimal valor,
            @RequestParam("paymentMethod") String paymentMethod,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

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
                return "redirect:" + payment.getPointOfInteraction().getTransactionData().getTicketUrl();
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

    @GetMapping("/payment-pending")
    public String showPaymentPendingPage() {
        return "auth/payment-pending";
    }

    // ****** 1. NOVO MÉTODO PARA A PÁGINA DE "AGUARDE" PÓS-PAGAMENTO APROVADO ******
    @GetMapping("/payment-success-processing")
    public String showPaymentSuccessProcessingPage() {
        return "auth/payment-success-processing";
    }

    // ****** 2. NOVO ENDPOINT DE API PARA VERIFICAR O STATUS DA ASSINATURA ******
    @GetMapping("/api/subscription/status")
    @ResponseBody
    public Map<String, Boolean> getSubscriptionStatus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Map.of("active", false);
        }
        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(authentication.getName());
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            boolean isActive = usuario.getAcessoValidoAte() != null && usuario.getAcessoValidoAte().isAfter(LocalDateTime.now());
            return Map.of("active", isActive);
        }
        return Map.of("active", false);
    }

    @PostMapping("/process-card-payment")
    public ResponseEntity<?> processCardPayment(
            @RequestBody PaymentRequestDTO paymentRequest,
            Authentication authentication) {

        try {
            if (paymentRequest.getValor().compareTo(new BigDecimal("10.00")) < 0) {
                return ResponseEntity.status(400).body(Map.of("error", "Valor mínimo: R$ 10,00."));
            }

            Optional<Usuario> optionalUsuario = usuarioService.findByUsername(authentication.getName());
            if (optionalUsuario.isEmpty()) {
                return ResponseEntity.status(500).body(Map.of("error", "Usuário não encontrado."));
            }
            Usuario usuario = optionalUsuario.get();

            Payment payment = mercadoPagoService.createCardPayment(
                    usuario,
                    paymentRequest.getToken(),
                    paymentRequest.getDescricao(),
                    paymentRequest.getValor(),
                    paymentRequest.getInstallments(),
                    paymentRequest.getPaymentMethodId(),
                    paymentRequest.getIssuerId()
            );

            // ****** 3. LÓGICA DE REDIRECIONAMENTO AJUSTADA ******
            if ("approved".equals(payment.getStatus())) {
                // Redireciona para a página de "aguarde" para esperar o webhook
                return ResponseEntity.ok(Map.of("redirectUrl", "/payment-success-processing"));
            } else if ("in_process".equals(payment.getStatus())) {
                // Redireciona para a página de "pagamento pendente em análise"
                return ResponseEntity.ok(Map.of("redirectUrl", "/payment-pending"));
            } else {
                // Para pagamentos rejeitados, volta com a mensagem de erro
                String errorRedirectUrl = "/subscription?error=payment_" + payment.getStatus()
                        + "&detail=" + payment.getStatusDetail();
                return ResponseEntity.ok(Map.of("redirectUrl", errorRedirectUrl));
            }
        } catch (MPException | MPApiException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao processar pagamento com Mercado Pago: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ocorreu um erro inesperado. Tente novamente mais tarde."));
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
            return "protected-content-page"; // Crie esta página se não existir
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