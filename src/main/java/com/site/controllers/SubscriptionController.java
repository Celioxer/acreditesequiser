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

    // ============================================================
    // STATUS DO USUÁRIO (API para o frontend verificar liberação)
    // ============================================================
    @GetMapping("/api/user/status")
    @ResponseBody
    public Mono<ResponseEntity<Map<String, String>>> getUsuarioStatus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Mono.just(ResponseEntity.status(401).body(Map.of("status", "UNAUTHENTICATED")));
        }

        try {
            Usuario usuario = usuarioService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            boolean acessoValido =
                    usuario.getAcessoValidoAte() != null &&
                            usuario.getAcessoValidoAte().isAfter(LocalDateTime.now());

            return Mono.just(ResponseEntity.ok(
                    Map.of("status", acessoValido ? "APPROVED" : "PENDING")
            ));

        } catch (Exception e) {
            logger.error("Erro ao verificar status", e);
            return Mono.just(ResponseEntity.status(500).body(Map.of("status", "ERROR")));
        }
    }

    // ============================================================
    // CHECKOUT (Renderiza as páginas de pagamento)
    // ============================================================
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

        Optional<Usuario> optUser = usuarioService.findByUsername(authentication.getName());
        if (optUser.isEmpty()) {
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

    // ============================================================
    // RENOVAÇÃO (Renderiza as páginas de pagamento para quem já é assinante)
    // ============================================================
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

        // Padronizando para optUser
        Optional<Usuario> optUser = usuarioService.findByUsername(authentication.getName());
        if (optUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Erro: usuário não encontrado.");
            return "redirect:/logout";
        }

        String descricao = "Apoio Mensal - Renovação de Assinatura";

        // Adiciona o usuário ao modelo (caso a página precise exibir nome, etc)
        model.addAttribute("usuario", optUser.get());
        model.addAttribute("valor", valor);
        model.addAttribute("descricao", descricao);

        if ("pix".equals(paymentMethod)) {
            return "auth/pix_payment";
        }

        if ("card".equals(paymentMethod)) {
            return "auth/card_payment";
        }

        redirectAttributes.addFlashAttribute("error", "Método inválido.");
        return "redirect:/conteudo-protegido";
    }

    // ============================================================
    // PROCESSAR ASSINATURA NO CARTÃO (FLUXO SEGURO DE 2 ETAPAS)
    // ============================================================
    @PostMapping("/process-card-payment")
    @ResponseBody
    public Mono<ResponseEntity<Map<String, Object>>> processCardPayment(
            @RequestBody PaymentRequestDTO paymentRequest,
            Authentication authentication
    ) {

        if (paymentRequest.getValor().compareTo(new BigDecimal("10.00")) < 0) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "Valor mínimo é R$ 10,00.")));
        }

        Optional<Usuario> optUser = usuarioService.findByUsername(authentication.getName());
        if (optUser.isEmpty()) {
            return Mono.just(ResponseEntity.status(500)
                    .body(Map.of("error", "Usuário não encontrado.")));
        }

        Usuario usuario = optUser.get();

        // 1. Cria Pagamento Inicial (Validação/Cobrança Única)
        return mercadoPagoWebClientService
                .criarPagamentoInicial(
                        usuario,
                        paymentRequest
                )
                .flatMap(paymentResponse -> {
                    String status = (String) paymentResponse.get("status");

                    // 2. Se aprovado, cria a Assinatura IMEDIATAMENTE (Síncrono)
                    if ("approved".equals(status)) {
                        return mercadoPagoWebClientService.criarAssinatura(
                                usuario,
                                paymentRequest.getToken(),
                                paymentRequest.getDescricao(),
                                paymentRequest.getValor()
                        ).map(subscriptionResponse -> {
                            return ResponseEntity.ok((Map<String, Object>) subscriptionResponse);
                        });

                    } else if ("in_process".equals(status)) {
                        // Se pendente, retorna OK para o front (usuário espera na tela de pendente)
                        // A assinatura será criada pelo Webhook quando o banco aprovar.
                        return Mono.just(ResponseEntity.ok(paymentResponse));
                    } else {
                        // Rejeitado (ex: saldo insuficiente, risco)
                        return Mono.just(ResponseEntity.status(400).body(paymentResponse));
                    }
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException ex) {
                        logger.warn("Erro Mercado Pago (Cartão): {} - {}",
                                ex.getStatusCode(), ex.getResponseBodyAsString());
                        // Retorna o erro original do MP
                        Map<String, Object> body = null;
                        try { body = ex.getResponseBodyAs(Map.class); } catch (Exception ignored) {}
                        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(body));
                    }
                    logger.error("Erro interno ao processar cartão", throwable);
                    return Mono.just(ResponseEntity.status(500)
                            .body(Map.of("error", "Erro interno no servidor.")));
                });
    }

    // ============================================================
    // PROCESSAR PIX
    // ============================================================
    @GetMapping("/processar-pix")
    @ResponseBody
    public Mono<ResponseEntity<Map<String, Object>>> processarPix(
            @RequestParam BigDecimal valor,
            Authentication authentication) {

        Optional<Usuario> optUser = usuarioService.findByUsername(authentication.getName());
        if (optUser.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "Usuário não encontrado")));
        }

        Usuario usuario = optUser.get();

        return mercadoPagoWebClientService
                .iniciarPagamentoPix(usuario, "Assinatura Mensal", valor)
                .map(resp -> ResponseEntity.ok((Map<String, Object>) resp))
                .onErrorResume((Throwable throwable) -> {

                    if (throwable instanceof WebClientResponseException ex) {
                        logger.warn("Erro MP PIX: {} - {}",
                                ex.getStatusCode(), ex.getResponseBodyAsString());
                        Map<String, Object> body = null;
                        try { body = ex.getResponseBodyAs(Map.class); } catch (Exception ignored) {}
                        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(body));
                    }

                    logger.error("Erro interno PIX: {}", throwable.getMessage(), throwable);
                    return Mono.just(ResponseEntity.status(500)
                            .body(Map.of("error", "Erro interno no servidor.")));
                });
    }

    // ============================================================
    // PÁGINAS DE SISTEMA
    // ============================================================
    @GetMapping("/pagamento-sucesso")
    public String success() { return "auth/payment-success"; }

    @GetMapping("/subscription")
    public String subscription() { return "auth/subscription"; }

    @GetMapping("/payment-pending")
    public String showPendingPage() { return "auth/payment-pending"; }

    // ============================================================
    // EPISÓDIOS
    // ============================================================
    @GetMapping("/episodios")
    public String episodios(Authentication auth, Model model) {
        if (auth != null && auth.isAuthenticated()) {
            Optional<Usuario> optUser = usuarioService.findByUsername(auth.getName());
            optUser.ifPresent(usuario -> model.addAttribute("usuario", usuario));
        }
        return "episodios";
    }

    @GetMapping("/conteudo-protegido")
    public String conteudoProtegido(
            Authentication auth,
            Model model,
            RedirectAttributes attrs) {

        Optional<Usuario> optUser = usuarioService.findByUsername(auth.getName());
        if (optUser.isEmpty()) {
            attrs.addFlashAttribute("error", "Usuário não encontrado.");
            return "redirect:/subscription";
        }

        Usuario usuario = optUser.get();

        if (usuario.getAcessoValidoAte() != null &&
                usuario.getAcessoValidoAte().isAfter(LocalDateTime.now())) {

            model.addAttribute("usuario", usuario);
            return "protected-content-page";
        }

        attrs.addFlashAttribute("error", "Assinatura expirada.");
        return "redirect:/subscription";
    }
}