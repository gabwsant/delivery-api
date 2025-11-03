package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.dto.projection.RelatorioFaturamentoCategoria;
import com.deliverytech.delivery_api.dto.projection.RelatorioRankingClientes;
import com.deliverytech.delivery_api.dto.projection.RelatorioVendasRestaurante;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT DISTINCT p FROM Pedido p " +
            "LEFT JOIN FETCH p.itens " +
            "WHERE p.cliente.id = :clienteId")
    List<Pedido> findByClienteWithItens(@Param("clienteId") Long clienteId);

    @Query("SELECT DISTINCT p FROM Pedido p " +
            "LEFT JOIN FETCH p.itens " +
            "WHERE p.cliente.id = :clienteId AND p.status = :status")
    List<Pedido> findByClienteAndStatusWithItens(@Param("clienteId") Long clienteId,
                                                 @Param("status") String status);


    @Query("SELECT DISTINCT p FROM Pedido p " +
            "LEFT JOIN FETCH p.itens " + // CORRIGIDO
            "WHERE p.dataPedido BETWEEN :inicio AND :fim")
    List<Pedido> findByPeriodoWithItens(@Param("inicio") LocalDateTime inicio,
                                        @Param("fim") LocalDateTime fim);

    @Query("SELECT DISTINCT p FROM Pedido p " +
            "LEFT JOIN FETCH p.itens " +
            "WHERE p.status = :status")
    List<Pedido> findByStatusWithItens(@Param("status") String status);

    @Query("""
        SELECT p.restaurante.nome, COUNT(p)
        FROM Pedido p
        WHERE p.dataPedido BETWEEN :inicio AND :fim
        GROUP BY p.restaurante.nome
        ORDER BY COUNT(p) DESC
        """)
    List<Object[]> relatorioPedidosPorRestaurante(@Param("inicio") LocalDateTime inicio,
                                                  @Param("fim") LocalDateTime fim);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id") // CORRIGIDO
    Pedido findByIdWithItens(@Param("id") Long id); // Nome do método alterado para clareza

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(String status);

    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    /**
     * 3.1: Pedidos com valor acima de X
     */
    @Query("SELECT p FROM Pedido p WHERE p.total > :valor")
    List<Pedido> findPedidosComValorAcimaDe(@Param("valor") BigDecimal valor);

    /**
     * 3.1: Relatório por período e status
     */
    @Query("SELECT p FROM Pedido p " +
            "WHERE p.dataPedido BETWEEN :inicio AND :fim " +
            "AND p.status = :status")
    List<Pedido> findRelatorioPorPeriodoEStatus(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("status") String status
    );

    /**
     * 3.1: Total de vendas por restaurante (Usando Projeção 3.3)
     */
    @Query("SELECT p.restaurante.nome AS restauranteNome, SUM(p.total) AS totalVendas " +
            "FROM Pedido p " +
            "WHERE p.status = 'ENTREGUE' " + // Boa prática: contar apenas pedidos concluídos
            "GROUP BY p.restaurante.nome " +
            "ORDER BY totalVendas DESC")
    List<RelatorioVendasRestaurante> getRelatorioVendasPorRestaurante();

    // --- ATIVIDADE 3.2 (Feitas com JPQL, não Native) ---

    /**
     * 3.2: Ranking de clientes por nº de pedidos (Usando Projeção 3.3)
     */
    @Query("SELECT p.cliente.nome AS nomeCliente, COUNT(p.id) AS totalPedidos " +
            "FROM Pedido p " +
            "GROUP BY p.cliente.nome " +
            "ORDER BY totalPedidos DESC")
    List<RelatorioRankingClientes> getRelatorioRankingClientes();

    /**
     * 3.2: Faturamento por categoria (Usando Projeção 3.3)
     */
    @Query("SELECT p.restaurante.categoria AS categoria, SUM(p.total) AS totalVendas " +
            "FROM Pedido p " +
            "WHERE p.status = 'ENTREGUE' " +
            "GROUP BY p.restaurante.categoria " +
            "ORDER BY totalVendas DESC")
    List<RelatorioFaturamentoCategoria> getRelatorioFaturamentoPorCategoria();
}