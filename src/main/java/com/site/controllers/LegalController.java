package com.site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/politica-de-privacidade")
    public String paginaPoliticaDePrivacidade() {
        // Isso vai procurar e retornar o arquivo "politica-de-privacidade.html"
        // que está dentro da pasta /resources/templates/
        return "politica-de-privacidade";
    }

    @GetMapping("/politica-de-cookies")
    public String paginaPoliticaDeCookies() {
        return "politica-de-cookies";
    }

    @GetMapping("/termos-e-condicoes")
    public String paginaTermosECondicoes() {
        return "termos-e-condicoes";
    }
}