package com.site.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.site.services.MercadoPagoService;
import com.site.models.Usuario;
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
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDateTime;

@Controller
public class SubscriptionController {

    private final MercadoPagoService mercadoPagoService;
    private final UsuarioService usuarioService;

    public SubscriptionController(MercadoPagoService mercadoPagoService, UsuarioService usuarioService) {
        this.mercadoPagoService = mercadoPagoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Exibe a página de seleção de planos.
     */
    @GetMapping("/subscription")
    public String showSubscriptionPage() {
        return "auth/subscription";
    }

    /**
     * Processa a seleção do plano e método de pagamento.
     * Inclui a lógica para a colaboração com valor dinâmico.
     */
    @GetMapping("/checkout")
    public String checkout(
            @RequestParam("plan") String plan,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "valor", required = false) BigDecimal valor,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String username = authentication.getName();
        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(username);

        if (!optionalUsuario.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Erro: Usuário não encontrado.");
            return "redirect:/subscription";
        }

        Usuario usuario = optionalUsuario.get();

        String descricao;

        if (Objects.equals(plan, "basic")) {
            valor = new BigDecimal("10.00");
            descricao = "Acredite se quiser, mas vou dar uma força!";
        } else if (Objects.equals(plan, "premium")) {
            if (valor == null || valor.compareTo(BigDecimal.TEN) < 0) {
                redirectAttributes.addFlashAttribute("error", "O valor mínimo para o plano de colaboração é R$ 10,00.");
                return "redirect:/subscription";
            }
            descricao = "To acreditando mais ainda !!!";
        } else {
            redirectAttributes.addFlashAttribute("error", "Plano de assinatura inválido.");
            return "redirect:/subscription";
        }

        if ("pix".equals(paymentMethod)) {
            try {
                // A chamada foi corrigida para passar o objeto 'usuario' completo
                Payment payment = mercadoPagoService.createPixPayment(usuario, descricao, valor);
                String pixUrl = payment.getPointOfInteraction().getTransactionData().getTicketUrl();
                return "redirect:" + pixUrl;
            } catch (MPException | MPApiException e) {
                redirectAttributes.addFlashAttribute("error", "Erro ao criar pagamento Pix: " + e.getMessage());
                return "redirect:/subscription";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Método de pagamento inválido.");
            return "redirect:/subscription";
        }
    }

    /**
     * Lida com o acesso a conteúdo protegido com restrições.
     * Permite que usuários com assinatura ativa acessem a página.
     */
    @GetMapping("/conteudo-protegido")
    public String getProtectedContent(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Optional<Usuario> optionalUsuario = usuarioService.findByUsername(username);

        if (!optionalUsuario.isPresent()) {
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

    /**
     * Rota para a página de episódios. A verificação de acesso é feita pelo Security.
     */
    @GetMapping("/episodios")
    public String showEpisodesPage() {
        return "episodios";
    }
}
