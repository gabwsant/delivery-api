package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar pedidos de um cliente específico, carregando produtos
    @Query("SELECT DISTINCT p FROM Pedido p " +
            "JOIN FETCH p.produtos " +
            "WHERE p.cliente.id = :clienteId")
    List<Pedido> findByClienteWithProdutos(@Param("clienteId") Long clienteId);

    // Buscar pedidos de um cliente filtrando por status, carregando produtos
    @Query("SELECT DISTINCT p FROM Pedido p " +
            "JOIN FETCH p.produtos " +
            "WHERE p.cliente.id = :clienteId AND p.status = :status")
    List<Pedido> findByClienteAndStatusWithProdutos(@Param("clienteId") Long clienteId,
                                                    @Param("status") String status);

    // Buscar pedidos por intervalo de datas, carregando produtos
    @Query("SELECT DISTINCT p FROM Pedido p " +
            "JOIN FETCH p.produtos " +
            "WHERE p.dataPedido BETWEEN :inicio AND :fim")
    List<Pedido> findByPeriodoWithProdutos(@Param("inicio") LocalDateTime inicio,
                                           @Param("fim") LocalDateTime fim);

    // Buscar pedidos por status específico, carregando produtos
    @Query("SELECT DISTINCT p FROM Pedido p " +
            "JOIN FETCH p.produtos " +
            "WHERE p.status = :status")
    List<Pedido> findByStatusWithProdutos(@Param("status") String status);

    // Relatório: total de pedidos por restaurante em um período
    @Query("""
        SELECT p.restaurante.nome, COUNT(p)
        FROM Pedido p
        WHERE p.dataPedido BETWEEN :inicio AND :fim
        GROUP BY p.restaurante.nome
        ORDER BY COUNT(p) DESC
        """)
    List<Object[]> relatorioPedidosPorRestaurante(@Param("inicio") LocalDateTime inicio,
                                                  @Param("fim") LocalDateTime fim);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.produtos WHERE p.id = :id")
    Pedido findByIdWithProdutos(@Param("id") Long id);
}
