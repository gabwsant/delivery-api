package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.projection.*;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.ItemPedidoRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RelatorioService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public RelatorioService(PedidoRepository pedidoRepository,
                            ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<RelatorioVendasRestaurante> getVendasPorRestaurante() {
        return pedidoRepository.getRelatorioVendasPorRestaurante();
    }

    @Transactional(readOnly = true)
    public List<RelatorioProdutosMaisVendidos> getProdutosMaisVendidos() {
        return itemPedidoRepository.getRelatorioProdutosMaisVendidos();
    }

    @Transactional(readOnly = true)
    public List<RelatorioRankingClientes> getRankingClientes() {
        return pedidoRepository.getRelatorioRankingClientes();
    }

    @Transactional(readOnly = true)
    public List<RelatorioFaturamentoCategoria> getFaturamentoPorCategoria() {
        return pedidoRepository.getRelatorioFaturamentoPorCategoria();
    }

    // Dentro de RelatorioService.java

// ... (seus métodos existentes) ...

    /**
     * Suporta GET /api/relatorios/pedidos-por-periodo
     */
    @Transactional(readOnly = true)
    public List<Pedido> getPedidosPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new RegraNegocioException("As datas de início e fim são obrigatórias.");
        }
        // Usando o método Query Method do Spring Data JPA (findByDataPedidoBetween)
        // Se quiser usar o método com FETCH que já existia:
        // return pedidoRepository.findByPeriodoWithItens(dataInicio, dataFim);

        // Usando o Query Method mais simples
        return pedidoRepository.findByDataPedidoBetween(dataInicio, dataFim);
    }
}