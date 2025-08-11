package com.site.controllers;

import com.site.models.Usuario;
import com.site.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid Usuario usuario,
            BindingResult result,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        if (!usuario.getSenha().equals(confirmPassword)) {
            model.addAttribute("error", "As senhas não coincidem.");
            return "auth/register";
        }

        usuarioService.registrarUsuario(usuario);
        return "redirect:/login?registroSucesso";
    }

    @ExceptionHandler(Exception.class)
    public String handleError(Model model, Exception ex) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}
