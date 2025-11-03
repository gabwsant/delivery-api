package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.dto.projection.RelatorioProdutosMaisVendidos;
import com.deliverytech.delivery_api.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    /**
     * 3.2: Produtos mais vendidos (Usando Projeção 3.3)
     * Isso é feito com JPQL, muito melhor que SQL Nativo!
     */
    @Query("SELECT ip.produto.nome AS nomeProduto, SUM(ip.quantidade) AS quantidadeVendida " +
            "FROM ItemPedido ip " +
            "JOIN ip.pedido p " + // Garante que o item pertence a um pedido
            "WHERE p.status = 'ENTREGUE' " + // Boa prática: contar apenas pedidos concluídos
            "GROUP BY ip.produto.nome " +
            "ORDER BY quantidadeVendida DESC")
    List<RelatorioProdutosMaisVendidos> getRelatorioProdutosMaisVendidos();
}