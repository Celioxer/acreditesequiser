package com.site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mercadopago")
public class RetornoController {

    @GetMapping("/retorno")
    public String retorno() {
        // Essa rota é chamada automaticamente pelo Mercado Pago após o pagamento
        // Aqui futuramente você pode adicionar a lógica para validar o pagamento,
        // atualizar o banco, etc.
        return "mp-retorno";
    }
}
