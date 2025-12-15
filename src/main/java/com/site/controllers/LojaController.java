package com.site.controllers;

import com.site.models.Produto;
import com.site.services.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LojaController {

    private final ProdutoService produtoService;

    public LojaController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // --- PÁGINA PÚBLICA DA LOJA ---
    @GetMapping("/loja")
    public String loja(Model model) {
        model.addAttribute("produtos", produtoService.listarProdutosLoja());
        return "loja"; // Template loja.html
    }

    // --- ÁREA DO VENDEDOR (ADMIN) ---
    // (Lembre-se de restringir isso no SecurityConfig para ROLE_ADMIN)
    @GetMapping("/admin/produtos")
    public String gerenciarProdutos(Model model) {
        model.addAttribute("produtos", produtoService.listarTodosAdmin());
        model.addAttribute("novoProduto", new Produto());
        return "admin/produtos"; // Template de gerenciamento
    }

    @PostMapping("/admin/produtos/salvar")
    public String salvarProduto(@ModelAttribute Produto produto, RedirectAttributes attrs) {
        produtoService.salvarProduto(produto);
        attrs.addFlashAttribute("success", "Produto salvo com sucesso!");
        return "redirect:/admin/produtos";
    }

    @GetMapping("/admin/produtos/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        model.addAttribute("produtos", produtoService.listarTodosAdmin());
        model.addAttribute("novoProduto", produtoService.buscarPorId(id)); // Reutiliza o form
        return "admin/produtos";
    }

    @GetMapping("/admin/produtos/excluir/{id}")
    public String excluirProduto(@PathVariable Long id, RedirectAttributes attrs) {
        produtoService.excluirProduto(id);
        attrs.addFlashAttribute("success", "Produto removido.");
        return "redirect:/admin/produtos";
    }
}