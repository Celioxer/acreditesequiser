package com.site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {

    @GetMapping("/")
    public String home() {
        return "home"; // o Spring procura por home.html em /templates
    }
    @GetMapping("/episodios")
    public String episodios() {
        return "episodios";
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/contato")
    public String contato() {
        return "contato";
    }
}