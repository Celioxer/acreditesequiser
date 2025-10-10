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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
public class SubscriptionController {

    private final MercadoPagoService mercadoPagoService;
    private final UsuarioService usuarioService;

    // Construtor
    public SubscriptionController(MercadoPagoService mercadoPagoService, UsuarioService usuarioService) {
        this.mercadoPagoService = mercadoPagoService;
        this.usuarioService = usuarioService;
    }

    // Método existente para NOVO PAGAMENTO (checkout)
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
        // **IMPORTANTE:** Descrição clara para diferenciar da renovação
        String descricao = "Apoio Mensal - Novo Pagamento";

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
            model.addAttribute("descricao", descricao); // Passa a descrição para o formulário de cartão
            return "auth/card_payment";
        } else {
            redirectAttributes.addFlashAttribute("error", "Método de pagamento inválido.");
            return "redirect:/subscription";
        }
    }

    // NOVO MÉTODO PARA RENOVAÇÃO ANTECIPADA OU TARDIA
    @GetMapping("/assinatura/renovar")
    public String renewSubscription(
            @RequestParam("valor") BigDecimal valor,
            @RequestParam("paymentMethod") String paymentMethod,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) { // Adiciona Model para Cartão

        if (valor.compareTo(new BigDecimal("10.00")) < 0) {
            redirectAttributes.addFlashAttribute("error", "O valor do apoio deve ser de no mínimo R$ 10,00.");
            return "redirect:/conteudo-protegido";
        }

        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(authentication.getName());
        if (optionalUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Erro: Usuário não encontrado.");
            return "redirect:/logout";
        }
        Usuario usuario = optionalUsuario.get();

        // **DESCRIÇÃO CRÍTICA:** Indica que é uma renovação para ser tratada no Webhook/processCardPayment
        String descricao = "Apoio Mensal - Renovação de Assinatura";

        if ("pix".equals(paymentMethod)) {
            try {
                // Reutiliza o método de criação de pagamento PIX
                Payment payment = mercadoPagoService.createPixPayment(usuario, descricao, valor);
                return "redirect:" + payment.getPointOfInteraction().getTransactionData().getTicketUrl();
            } catch (MPException | MPApiException e) {
                redirectAttributes.addFlashAttribute("error", "Erro ao criar pagamento Pix para renovação: " + e.getMessage());
                return "redirect:/conteudo-protegido";
            }
        } else if ("card".equals(paymentMethod)) {
            // Se for Cartão, vai para a página do formulário
            model.addAttribute("valor", valor);
            model.addAttribute("descricao", descricao); // Passa a descrição de 'Renovação'
            return "auth/card_payment";
        } else {
            redirectAttributes.addFlashAttribute("error", "Método de pagamento inválido.");
            return "redirect:/conteudo-protegido";
        }
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

            String descricao = paymentRequest.getDescricao();

            Payment payment = mercadoPagoService.createCardPayment(
                    usuario,
                    paymentRequest.getToken(),
                    descricao,
                    paymentRequest.getValor(),
                    paymentRequest.getInstallments(),
                    paymentRequest.getPaymentMethodId(),
                    paymentRequest.getIssuerId()
            );

            if ("approved".equals(payment.getStatus())) {
                int planoDuracaoDias = 30;
                usuarioService.updateSubscriptionStatus(usuario.getEmail(), planoDuracaoDias);

                // ***** REDIRECIONAMENTO ALTERADO PARA PÁGINA DE SUCESSO *****
                return ResponseEntity.ok(Map.of("redirectUrl", "/pagamento-sucesso"));
            } else if ("in_process".equals(payment.getStatus())) {
                return ResponseEntity.ok(Map.of("redirectUrl", "/payment-pending"));
            } else {
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

    /**
     * NOVO ENDPOINT: Exibe a página de sucesso após o pagamento (com ou sem logout automático).
     */
    @GetMapping("/pagamento-sucesso")
    public String showPaymentSuccessPage() {
        return "auth/payment-success"; // Você deve criar este template HTML
    }

    @GetMapping("/subscription")
    public String showSubscriptionPage() {
        return "auth/subscription";
    }

    @GetMapping("/payment-pending")
    public String showPaymentPendingPage() {
        return "auth/payment-pending";
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

        if (usuario.getAcessoValidoAte() != null && usuario.getAcessoValidoAte().isAfter(LocalDateTime.now())) {
            model.addAttribute("usuario", usuario);
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