package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar pedidos de um cliente específico
    List<Pedido> findByCliente(Cliente cliente);

    // Buscar pedidos de um cliente filtrando por status
    List<Pedido> findByClienteAndStatus(Cliente cliente, String status);

    // Buscar pedidos por intervalo de datas
    @Query("SELECT p FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim")
    List<Pedido> findByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Buscar por status específico
    List<Pedido> findByStatus(String status);

    // Relatório: total de pedidos por restaurante em um período
    @Query("""
        SELECT p.restaurante.nome, COUNT(p)
        FROM Pedido p
        WHERE p.dataPedido BETWEEN :inicio AND :fim
        GROUP BY p.restaurante.nome
        ORDER BY COUNT(p) DESC
        """)
    List<Object[]> relatorioPedidosPorRestaurante(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
