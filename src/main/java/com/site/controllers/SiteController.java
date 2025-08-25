package com.site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "home"; // Página inicial acessível a todos
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/contato")
    public String contato() {
        return "contato";
    }

   // @GetMapping("/apoiadores")
   // public String apoiadores() {
     //   return "apoiadores";

}
