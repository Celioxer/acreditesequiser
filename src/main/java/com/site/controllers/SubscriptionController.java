package com.site.controllers;

import com.site.dto.PaymentRequestDTO;
import com.site.models.Usuario;
import com.site.services.MercadoPagoWebClientService;
import com.site.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
public class SubscriptionController {

    private final MercadoPagoWebClientService mercadoPagoWebClientService;
    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    public SubscriptionController(
            MercadoPagoWebClientService mercadoPagoWebClientService,
            UsuarioService usuarioService
    ) {
        this.mercadoPagoWebClientService = mercadoPagoWebClientService;
        this.usuarioService = usuarioService;
    }

    // ... (getUsuarioStatus, checkout, renovarAssinatura e processarPix IGUAIS) ...
    // Vou incluir apenas para o arquivo ficar completo e seguro para cópia

    @GetMapping("/api/user/status")
    @ResponseBody
    public Mono<ResponseEntity<Map<String, String>>> getUsuarioStatus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Mono.just(ResponseEntity.status(401).body(Map.of("status", "UNAUTHENTICATED")));
        }
        try {
            Usuario usuario = usuarioService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
            boolean acessoValido = usuario.getAcessoValidoAte() != null &&
                    usuario.getAcessoValidoAte().isAfter(LocalDateTime.now());
            if (acessoValido && usuario.getRole() == Usuario.Role.SUBSCRIBER) {
                return Mono.just(ResponseEntity.ok(Map.of("status", "APPROVED")));
            } else {
                return Mono.just(ResponseEntity.ok(Map.of("status", "PENDING")));
            }
        } catch (Exception e) {
            logger.error("Erro ao verificar status do usuário", e);
            return Mono.just(ResponseEntity.status(500).body(Map.of("status", "ERROR")));
        }
    }

    @GetMapping("/checkout")
    public String checkout(
            @RequestParam BigDecimal valor,
            @RequestParam String paymentMethod,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (valor.compareTo(new BigDecimal("10.00")) < 0) {
            redirectAttributes.addFlashAttribute("error", "Valor mínimo é R$ 10,00.");
            return "redirect:/subscription";
        }
        Optional<Usuario> optUsuario = usuarioService.findByUsername(authentication.getName());
        if (optUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado.");
            return "redirect:/subscription";
        }
        model.addAttribute("valor", valor);
        model.addAttribute("descricao", "Assinatura Mensal");
        if (paymentMethod.equals("pix")) return "auth/pix_payment";
        if (paymentMethod.equals("card")) return "auth/card_payment";
        redirectAttributes.addFlashAttribute("error", "Método inválido.");
        return "redirect:/subscription";
    }

    @GetMapping("/assinatura/renovar")
    public String renovarAssinatura(
            @RequestParam("valor") BigDecimal valor,
            @RequestParam("paymentMethod") String paymentMethod,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (valor.compareTo(new BigDecimal("10.00")) < 0) {
            redirectAttributes.addFlashAttribute("error", "Valor mínimo é R$ 10,00.");
            return "redirect:/conteudo-protegido";
        }
        Optional<Usuario> optUsuario = usuarioService.findByUsername(authentication.getName());
        if (optUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Erro: usuário não encontrado.");
            return "redirect:/logout";
        }
        model.addAttribute("usuario", optUsuario.get()); // Dados para o menu
        String descricao = "Apoio Mensal - Renovação de Assinatura";
        if ("pix".equals(paymentMethod)) {
            model.addAttribute("valor", valor);
            return "auth/pix_payment";
        }
        if ("card".equals(paymentMethod)) {
            model.addAttribute("valor", valor);
            model.addAttribute("descricao", descricao);
            return "auth/card_payment";
        }
        redirectAttributes.addFlashAttribute("error", "Método inválido.");
        return "redirect:/conteudo-protegido";
    }

    @GetMapping("/processar-pix")
    @ResponseBody
    public Mono<ResponseEntity<Map<String, Object>>> processarPix(
            @RequestParam BigDecimal valor,
            Authentication authentication) {
        Optional<Usuario> optUser = usuarioService.findByUsername(authentication.getName());
        if (optUser.isEmpty()) return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Usuário não encontrado")));

        return mercadoPagoWebClientService.iniciarPagamentoPix(optUser.get(), "Assinatura Mensal", valor)
                .map(resp -> ResponseEntity.ok((Map<String, Object>) resp))
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException ex) {
                        logger.warn("Erro MP PIX: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                        Map<String, Object> body = null;
                        try { body = ex.getResponseBodyAs(Map.class); } catch (Exception ignored) {}
                        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(body));
                    }
                    logger.error("Erro interno PIX: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.status(500).body(Map.of("error", "Erro interno no servidor.")));
                });
    }

    // ============================================================
    // ✅ PROCESSAR PAGAMENTO COM CARTÃO (Volta para 1 Passo)
    // ============================================================
    @PostMapping("/process-card-payment")
    @ResponseBody
    public Mono<ResponseEntity<Map<String, Object>>> processCardPayment(
            @RequestBody PaymentRequestDTO paymentRequest,
            Authentication authentication) {

        if (paymentRequest.getValor().compareTo(new BigDecimal("10.00")) < 0) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Valor mínimo é R$ 10,00.")));
        }

        Optional<Usuario> optUser = usuarioService.findByUsername(authentication.getName());
        if (optUser.isEmpty()) {
            return Mono.just(ResponseEntity.status(500).body(Map.of("error", "Usuário não encontrado.")));
        }

        // Chamamos DIRETAMENTE a criação da assinatura.
        // O token está novo e válido.
        // O status não será forçado, então se der pending, o MP aceita.
        return mercadoPagoWebClientService
                .criarAssinatura(
                        optUser.get(),
                        paymentRequest.getToken(),
                        paymentRequest.getDescricao(),
                        paymentRequest.getValor()
                )
                .map(resp -> ResponseEntity.ok((Map<String, Object>) resp))
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException ex) {
                        logger.warn("Erro MP Cartão: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                        Map<String, Object> errorBody = null;
                        try { errorBody = ex.getResponseBodyAs(Map.class); } catch (Exception ignored) {}
                        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(errorBody));
                    }
                    logger.error("Erro interno Cartão: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.status(500).body(Map.of("error", "Erro interno no servidor.")));
                });
    }

    @GetMapping("/pagamento-sucesso")
    public String success() { return "auth/payment-success"; }
    @GetMapping("/subscription")
    public String subscription() { return "auth/subscription"; }
    @GetMapping("/payment-pending")
    public String showPendingPage() { return "auth/payment-pending"; }
    @GetMapping("/episodios")
    public String episodios(Authentication auth, Model model) {
        if (auth != null && auth.isAuthenticated()) {
            Optional<Usuario> opt = usuarioService.findByUsername(auth.getName());
            opt.ifPresent(usuario -> model.addAttribute("usuario", usuario));
        }
        return "episodios";
    }
    @GetMapping("/conteudo-protegido")
    public String conteudoProtegido(Authentication auth, Model model, RedirectAttributes attrs) {
        Optional<Usuario> opt = usuarioService.findByUsername(auth.getName());
        if (opt.isEmpty()) {
            attrs.addFlashAttribute("error", "Usuário não encontrado.");
            return "redirect:/subscription";
        }
        Usuario usuario = opt.get();
        if (usuario.getAcessoValidoAte() != null && usuario.getAcessoValidoAte().isAfter(LocalDateTime.now())) {
            model.addAttribute("usuario", usuario);
            return "protected-content-page";
        }
        attrs.addFlashAttribute("error", "Assinatura expirada.");
        return "redirect:/subscription";
    }
}