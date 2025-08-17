package com.site.controllers;

import com.site.models.Usuario;
import com.site.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    // Removido o campo 'mercadoPagoService'

    // O construtor agora só precisa do UsuarioService
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        // 1. Validação de Senha no Controller
        if (!usuario.getSenha().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "As senhas não coincidem.");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/register";
        }

        // 2. Validação de Erros de Formulário (Ex: campos vazios)
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erro de validação. Verifique os campos.");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/register";
        }

        // 3. Chamada ao Serviço para Registrar
        try {
            usuarioService.registrarUsuario(usuario);
            redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso! Faça login para continuar.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/register";
        }
    }

    // Removido o método @PostMapping("/login") e toda a sua lógica de pagamento
}