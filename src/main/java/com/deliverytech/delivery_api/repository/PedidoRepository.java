package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.dto.projection.RelatorioFaturamentoCategoria;
import com.deliverytech.delivery_api.dto.projection.RelatorioRankingClientes;
import com.deliverytech.delivery_api.dto.projection.RelatorioVendasRestaurante;
import com.deliverytech.delivery_api.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // =========================================================
    // 1. BUSCAS SIMPLES (SPRING DATA JPA CONVENTION)
    // Usadas para buscas rápidas que não necessitam dos detalhes do ItemPedido
    // =========================================================

    // Busca por ID de Cliente (usado para histórico, mas sem itens carregados por padrão)
    List<Pedido> findByClienteId(Long clienteId);

    // Busca por status
    List<Pedido> findByStatus(String status);

    // Busca por data (período)
    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    // Top 10 pedidos mais recentes
    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    // =========================================================
    // 2. BUSCAS DETALHADAS (COM FETCH JOIN PARA ITENS)
    // Usadas quando o Service exige os detalhes completos do pedido (itens, total)
    // =========================================================

    /**
     * Busca um pedido completo por ID, carregando os itens.
     * Substitui: findByIdWithItens
     */
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    Pedido findByIdCompleto(@Param("id") Long id);

    /**
     * Lista pedidos de um cliente, carregando os itens.
     * Substitui: findByClienteWithItens
     */
    @Query("SELECT DISTINCT p FROM Pedido p " +
            "LEFT JOIN FETCH p.itens " +
            "WHERE p.cliente.id = :clienteId")
    List<Pedido> findByClienteIdCompleto(@Param("clienteId") Long clienteId);

    /**
     * Lista pedidos de um restaurante, carregando os itens.
     * Substitui: findByRestauranteIdWithItens
     */
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.restaurante.id = :restauranteId")
    List<Pedido> findByRestauranteIdCompleto(@Param("restauranteId") Long restauranteId);

    /**
     * Busca pedidos com filtros dinâmicos de status e período, carregando os itens.
     * Unifica: findByPeriodoWithItens, findByStatusWithItens, listarComFiltros
     */
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens " +
            "WHERE (:status IS NULL OR p.status = :status) " +
            "AND (:dataInicio IS NULL OR p.dataPedido >= :dataInicio) " +
            "AND (:dataFim IS NULL OR p.dataPedido <= :dataFim) " +
            "ORDER BY p.dataPedido DESC")
    List<Pedido> listarComFiltrosCompleto(
            @Param("status") String status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    // =========================================================
    // 3. RELATÓRIOS (PROJEÇÕES E AGREGAÇÕES)
    // Mantidos como estão, pois são específicos para geração de relatórios
    // =========================================================

    /**
     * Relatório: Total de vendas por restaurante (Projeção)
     */
    @Query("SELECT p.restaurante.nome AS restauranteNome, SUM(p.total) AS totalVendas " +
            "FROM Pedido p " +
            "WHERE p.status = 'ENTREGUE' " +
            "GROUP BY p.restaurante.nome " +
            "ORDER BY totalVendas DESC")
    List<RelatorioVendasRestaurante> getRelatorioVendasPorRestaurante();

    /**
     * Relatório: Ranking de clientes por nº de pedidos (Projeção)
     */
    @Query("SELECT p.cliente.nome AS nomeCliente, COUNT(p.id) AS totalPedidos " +
            "FROM Pedido p " +
            "GROUP BY p.cliente.nome " +
            "ORDER BY totalPedidos DESC")
    List<RelatorioRankingClientes> getRelatorioRankingClientes();

    /**
     * Relatório: Faturamento por categoria (Projeção)
     */
    @Query("SELECT p.restaurante.categoria AS categoria, SUM(p.total) AS totalVendas " +
            "FROM Pedido p " +
            "WHERE p.status = 'ENTREGUE' " +
            "GROUP BY p.restaurante.categoria " +
            "ORDER BY totalVendas DESC")
    List<RelatorioFaturamentoCategoria> getRelatorioFaturamentoPorCategoria();

    // --- Métodos Remanescentes (MUITO Específicos ou Herdados) ---

    /**
     * Busca pedidos com valor acima de um limite.
     */
    @Query("SELECT p FROM Pedido p WHERE p.total > :valor")
    List<Pedido> findPedidosComValorAcimaDe(@Param("valor") BigDecimal valor);

    // O método 'findRelatorioPorPeriodoEStatus' é similar a 'listarComFiltrosCompleto'
    // mas retorna a entidade completa, sem a necessidade de itens (depende do uso).
    // Se precisar da versão sem itens, mantemos:
    @Query("SELECT p FROM Pedido p " +
            "WHERE p.dataPedido BETWEEN :inicio AND :fim " +
            "AND p.status = :status")
    List<Pedido> findRelatorioPorPeriodoEStatus(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("status") String status
    );

    // O método "relatorioPedidosPorRestaurante" que retorna Object[] também é mantido
    // se for exigido que retorne o nome e a contagem de pedidos no período (sem projeção)
    @Query("""
        SELECT p.restaurante.nome, COUNT(p)
        FROM Pedido p
        WHERE p.dataPedido BETWEEN :inicio AND :fim
        GROUP BY p.restaurante.nome
        ORDER BY COUNT(p) DESC
        """)
    List<Object[]> relatorioPedidosPorRestaurante(@Param("inicio") LocalDateTime inicio,
                                                  @Param("fim") LocalDateTime fim);
}