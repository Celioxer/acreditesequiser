package com.site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Este método serve a página HTML do painel
    @GetMapping
    public String adminPanelPage() {
        return "admin/administrador"; // Caminho para: templates/admin/administrador.html
    }
}