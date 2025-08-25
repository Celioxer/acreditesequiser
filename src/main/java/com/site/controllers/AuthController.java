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

        if (!usuario.getSenha().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "As senhas não coincidem.");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/register";
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erro de validação. Verifique os campos.");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/register";
        }

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

    // =================================================================
    // <<< ENDPOINTS NOVOS PARA REDEFINIÇÃO DE SENHA >>>
    // =================================================================

    // Mostra o formulário para inserir o e-mail
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    // Processa a solicitação de redefinição de senha
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.createPasswordResetTokenForUser(email);
            redirectAttributes.addFlashAttribute("success", "Se uma conta com este e-mail existir, um link para redefinição de senha foi enviado.");
        } catch (RuntimeException e) {
            // Mostra a mesma mensagem de sucesso mesmo se o e-mail não existir, por segurança.
            redirectAttributes.addFlashAttribute("success", "Se uma conta com este e-mail existir, um link para redefinição de senha foi enviado.");
        }
        return "redirect:/forgot-password";
    }

    // Mostra o formulário para inserir a nova senha
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // Opcional: Você pode adicionar uma validação aqui para ver se o token é válido antes de mostrar a página
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    // Processa a nova senha
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "As senhas não coincidem.");
            return "redirect:/reset-password?token=" + token;
        }
        try {
            usuarioService.resetPassword(token, password);
            redirectAttributes.addFlashAttribute("success", "Sua senha foi redefinida com sucesso! Você já pode fazer login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reset-password?token=" + token;
        }
    }
}