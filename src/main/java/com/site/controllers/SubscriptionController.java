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

    // ------------------------------------------------------------
    // STATUS DO USUÁRIO
    // ------------------------------------------------------------
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

    // ------------------------------------------------------------
    // CHECKOUT
    // ------------------------------------------------------------
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

    // ------------------------------------------------------------
    // PROCESSAR ASSINATURA NO CARTÃO
    // ------------------------------------------------------------
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

        return mercadoPagoWebClientService
                .criarAssinatura(
                        usuario,
                        paymentRequest.getToken(),
                        paymentRequest.getDescricao(),
                        paymentRequest.getValor()
                )
                .map(resp -> ResponseEntity.ok((Map<String, Object>) resp))
                .onErrorResume(throwable -> {

                    if (throwable instanceof WebClientResponseException ex) {
                        logger.warn("Erro Mercado Pago (ASSINATURA): {} - {}",
                                ex.getStatusCode(), ex.getResponseBodyAsString());

                        return Mono.just(ResponseEntity
                                .status(ex.getStatusCode())
                                .body(ex.getResponseBodyAs(Map.class)));
                    }

                    logger.error("Erro interno ao criar assinatura", throwable);

                    return Mono.just(ResponseEntity.status(500)
                            .body(Map.of("error", "Erro interno no servidor.")));
                });
    }

    // ------------------------------------------------------------
    // PROCESSAR PIX
    // ------------------------------------------------------------
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
                        return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                .body(ex.getResponseBodyAs(Map.class)));
                    }

                    logger.error("Erro interno PIX: {}", throwable.getMessage(), throwable);
                    return Mono.just(ResponseEntity.status(500)
                            .body(Map.of("error", "Erro interno no servidor.")));
                });
    }

    // ------------------------------------------------------------
    // PÁGINAS
    // ------------------------------------------------------------
    @GetMapping("/pagamento-sucesso")
    public String success() { return "auth/payment-success"; }

    @GetMapping("/subscription")
    public String subscription() { return "auth/subscription"; }
}
