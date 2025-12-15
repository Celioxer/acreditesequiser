package com.site.repositories;

import com.site.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Busca apenas produtos ativos e com estoque para a loja pública
    List<Produto> findByAtivoTrueAndEstoqueGreaterThan(Integer estoqueMinimo);

    // Busca todos (para o admin)
    List<Produto> findAllByOrderByDataCriacaoDesc();
}