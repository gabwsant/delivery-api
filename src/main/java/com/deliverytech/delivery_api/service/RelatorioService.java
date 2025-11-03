package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.projection.*;
import com.deliverytech.delivery_api.repository.ItemPedidoRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}