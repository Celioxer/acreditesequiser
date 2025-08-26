package com.site.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.dto.PaymentRequestDTO; // <-- IMPORTAÇÃO DA NOVA CLASSE
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

    /**
     * Lida com a requisição POST do formulário de cartão de forma segura e moderna.
     */
    @PostMapping("/process-card-payment")
    // ****** ALTERAÇÃO PRINCIPAL AQUI ******
    // Trocamos o Map por nossa classe DTO, tornando o código mais seguro.
    public ResponseEntity<?> processCardPayment(
            @RequestBody PaymentRequestDTO paymentRequest,
            Authentication authentication) {

        try {
            // Validação do valor mínimo
            if (paymentRequest.getValor().compareTo(new BigDecimal("10.00")) < 0) {
                return ResponseEntity.status(400).body(Map.of("error", "Valor mínimo: R$ 10,00."));
            }

            Optional<Usuario> optionalUsuario = usuarioService.findByUsername(authentication.getName());
            if (optionalUsuario.isEmpty()) {
                return ResponseEntity.status(500).body(Map.of("error", "Usuário não encontrado."));
            }
            Usuario usuario = optionalUsuario.get();

            // Chamada ao serviço do Mercado Pago agora usa os dados do DTO
            Payment payment = mercadoPagoService.createCardPayment(
                    paymentRequest.getToken(),
                    usuario.getEmail(), // Usar o e-mail do usuário autenticado é mais seguro
                    usuario.getNome(),
                    usuario.getCpf(),
                    paymentRequest.getDescricao(),
                    paymentRequest.getValor(),
                    paymentRequest.getInstallments(),
                    paymentRequest.getPaymentMethodId()
            );

            int planoDuracaoDias = 30;

            if ("approved".equals(payment.getStatus())) {
                usuarioService.updateSubscriptionStatus(usuario.getEmail(), planoDuracaoDias);
                return ResponseEntity.ok(Map.of("redirectUrl", "/apoiadores"));
            } else {
                String errorRedirectUrl = "/subscription?error=payment_" + payment.getStatus()
                        + "&detail=" + payment.getStatusDetail();
                return ResponseEntity.ok(Map.of("redirectUrl", errorRedirectUrl));
            }
        } catch (MPException | MPApiException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao processar pagamento com Mercado Pago: " + e.getMessage()));
        } catch (Exception e) {
            // Logar o erro real no servidor é uma boa prática
            // log.error("Erro interno inesperado", e);
            return ResponseEntity.status(500).body(Map.of("error", "Ocorreu um erro inesperado. Tente novamente mais tarde."));
        }
    }

    // ... (restante dos seus métodos continua igual)
    @GetMapping("/conteudo-protegido")
    public String getProtectedContent(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        //...
        return "protected-content-page";
    }

    @GetMapping("/episodios")
    public String showEpisodesPage() {
        return "episodios";
    }
}