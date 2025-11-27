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
    // ✅ NOVO ENDPOINT DE VERIFICAÇÃO DE STATUS
    // (Necessário para a automação da página do PIX)
    // ============================================================

    /**
     * Endpoint para o frontend (JavaScript) "perguntar" se o usuário atual
     * já tem o acesso liberado (checando o banco de dados).
     */
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

    // ============================================================
    // ✅ CHECKOUT INICIAL (PIX ou CARTÃO)
    // ============================================================
    @GetMapping("/checkout")
    public String checkout(
            @RequestParam("valor") BigDecimal valor,
            @RequestParam("paymentMethod") String paymentMethod,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (valor.compareTo(new BigDecimal("10.00")) < 0) {
            redirectAttributes.addFlashAttribute("error", "Valor mínimo é R$ 10,00.");
            return "redirect:/subscription";
        }
        Optional<Usuario> optUsuario = usuarioService.findByUsername(authentication.getName());
        if (optUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Erro: usuário não encontrado.");
            return "redirect:/subscription";
        }
        Usuario usuario = optUsuario.get();
        String descricao = "Apoio Mensal - Novo Pagamento";
        if ("pix".equals(paymentMethod)) {
            model.addAttribute("valor", valor); // Correto
            return "auth/pix_payment";          // Correto
        }
        if ("card".equals(paymentMethod)) {
            model.addAttribute("valor", valor);
            model.addAttribute("descricao", descricao);
            return "auth/card_payment";
        }
        redirectAttributes.addFlashAttribute("error", "Método inválido.");
        return "redirect:/subscription";
    }

    // ============================================================
    // ✅ RENOVAÇÃO DE ASSINATURA (CORRIGIDO)
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
        Optional<Usuario> optUsuario = usuarioService.findByUsername(authentication.getName());
        if (optUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Erro: usuário não encontrado.");
            return "redirect:/logout";
        }
        Usuario usuario = optUsuario.get();
        String descricao = "Apoio Mensal - Renovação de Assinatura";

        // --- INÍCIO DA CORREÇÃO ---
        if ("pix".equals(paymentMethod)) {
            // ❌ ANTES (Errado)
            // return "redirect:/processar-pix?valor=" + valor;

            // ✅ DEPOIS (Correto)
            model.addAttribute("valor", valor);
            return "auth/pix_payment"; // Renderiza a página do QR Code
        }
        // --- FIM DA CORREÇÃO ---

        if ("card".equals(paymentMethod)) {
            model.addAttribute("valor", valor);
            model.addAttribute("descricao", descricao);
            return "auth/card_payment";
        }
        redirectAttributes.addFlashAttribute("error", "Método inválido.");
        return "redirect:/conteudo-protegido";
    }


    // ============================================================
    // ✅ PROCESSAR PIX (Corrigido com Bloco Único de Erro)
    // ============================================================

    @GetMapping("/processar-pix")
    @ResponseBody
    public Mono<ResponseEntity<Map<String, Object>>> processarPix(
            @RequestParam BigDecimal valor,
            Authentication authentication) {

        Optional<Usuario> optUser = usuarioService.findByUsername(authentication.getName());
        if (optUser.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Usuário não encontrado")));
        }

        Usuario usuario = optUser.get();
        String descricao = "Apoio Mensal";

        return mercadoPagoWebClientService
                .iniciarPagamentoPix(usuario, descricao, valor)
                .map(resp -> ResponseEntity.ok((Map<String, Object>) resp)) // SUCESSO
                .onErrorResume(throwable -> { // <-- CORREÇÃO AQUI: Captura TUDO
                    if (throwable instanceof WebClientResponseException) {
                        // ERRO 4xx/5xx vindo do Mercado Pago
                        WebClientResponseException ex = (WebClientResponseException) throwable;
                        logger.warn("Erro do Mercado Pago ao criar PIX: {} - Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                        return Mono.just(ResponseEntity
                                .status(ex.getStatusCode())
                                .body(ex.getResponseBodyAs(Map.class)));
                    } else {
                        // ERRO 500 Interno (NullPointer, etc)
                        logger.error("Erro interno ao processar PIX: {}", throwable.getMessage());
                        return Mono.just(ResponseEntity
                                .status(500)
                                .body(Map.of("error", "Erro interno no servidor.")));
                    }
                });
    }


    // ============================================================
    // ✅ PROCESSAR PAGAMENTO COM CARTÃO (Corrigido com Bloco Único de Erro)
    // ============================================================

    @PostMapping("/process-card-payment")
    @ResponseBody
    public Mono<ResponseEntity<Map<String, Object>>> processCardPayment(
            @RequestBody PaymentRequestDTO paymentRequest,
            Authentication authentication) {

        if (paymentRequest.getValor().compareTo(new BigDecimal("10.00")) < 0) {
            return Mono.just(ResponseEntity.badRequest().body(
                    Map.of("error", "Valor mínimo é R$ 10,00.")
            ));
        }

        Optional<Usuario> optUser = usuarioService.findByUsername(authentication.getName());
        if (optUser.isEmpty()) {
            return Mono.just(ResponseEntity.status(500).body(
                    Map.of("error", "Usuário não encontrado.")
            ));
        }

        Usuario usuario = optUser.get();

        return mercadoPagoWebClientService
                .criarAssinatura(
                        usuario,
                        paymentRequest.getToken(),
                        paymentRequest.getDescricao(),
                        paymentRequest.getValor()
                )
                .map(resp -> ResponseEntity.ok((Map<String, Object>) resp)) // SUCESSO
                .onErrorResume(throwable -> { // <-- CORREÇÃO AQUI: Captura TUDO
                    if (throwable instanceof WebClientResponseException) {
                        // ERRO 4xx/5xx vindo do Mercado Pago
                        WebClientResponseException ex = (WebClientResponseException) throwable;
                        logger.warn("Erro do Mercado Pago ao criar assinatura: {} - Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                        return Mono.just(ResponseEntity
                                .status(ex.getStatusCode())
                                .body(ex.getResponseBodyAs(Map.class)));
                    } else {
                        // ERRO 500 Interno (NullPointer, etc)
                        logger.error("Erro interno ao processar pagamento com cartão: {}", throwable.getMessage());
                        return Mono.just(ResponseEntity
                                .status(500)
                                .body(Map.of("error", "Erro interno no servidor.")));
                    }
                });
    }

    // ============================================================
    // ✅ PÁGINAS (Inalteradas)
    // ============================================================

    @GetMapping("/pagamento-sucesso")
    public String showSuccessPage() { return "auth/payment-success"; }

    @GetMapping("/subscription")
    public String subscriptionPage() { return "auth/subscription"; }

    @GetMapping("/payment-pending")
    public String showPendingPage() { return "auth/payment-pending"; }

    @GetMapping("/episodios")
    public String episodios() { return "episodios"; }

    @GetMapping("/conteudo-protegido")
    public String conteudoProtegido(
            Authentication auth,
            Model model,
            RedirectAttributes attrs) {

        Optional<Usuario> opt = usuarioService.findByUsername(auth.getName());
        if (opt.isEmpty()) {
            attrs.addFlashAttribute("error", "Usuário não encontrado.");
            return "redirect:/subscription";
        }
        Usuario usuario = opt.get();
        if (usuario.getAcessoValidoAte() != null &&
                usuario.getAcessoValidoAte().isAfter(LocalDateTime.now())) {
            model.addAttribute("usuario", usuario);
            return "protected-content-page";
        }
        attrs.addFlashAttribute("error", "Assinatura expirada.");
        return "redirect:/subscription";
    }
}