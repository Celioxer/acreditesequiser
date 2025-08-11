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

@Controller
public class AuthController {
    private final UsuarioService usuarioService;

    // Injeção de dependência correta
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Rota para exibir formulário de login
    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login"; // Caminho correto do template
    }

    // Rota para exibir formulário de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario()); // Objeto para binding correto
        return "auth/register"; // Caminho correto do template
    }

    @PostMapping("/register")
    public String registerUser(@Valid Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
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