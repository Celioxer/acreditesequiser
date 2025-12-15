package com.site.services;

import com.site.models.Produto;
import com.site.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarProdutosLoja() {
        return produtoRepository.findByAtivoTrueAndEstoqueGreaterThan(0);
    }

    public List<Produto> listarTodosAdmin() {
        return produtoRepository.findAllByOrderByDataCriacaoDesc();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    @Transactional
    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    @Transactional
    public void excluirProduto(Long id) {
        produtoRepository.deleteById(id);
    }

    @Transactional
    public void atualizarEstoque(Long id, int quantidadeVendida) {
        Produto produto = buscarPorId(id);
        int novoEstoque = produto.getEstoque() - quantidadeVendida;
        if (novoEstoque < 0) throw new RuntimeException("Estoque insuficiente");
        produto.setEstoque(novoEstoque);
        produtoRepository.save(produto);
    }
}