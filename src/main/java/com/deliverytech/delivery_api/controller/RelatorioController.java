package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.projection.RelatorioFaturamentoCategoria; // Importação necessária
import com.deliverytech.delivery_api.dto.projection.RelatorioProdutosMaisVendidos; // Importação necessária
import com.deliverytech.delivery_api.dto.projection.RelatorioRankingClientes; // Importação necessária
import com.deliverytech.delivery_api.dto.projection.RelatorioVendasRestaurante; // Importação necessária
import com.deliverytech.delivery_api.entity.Pedido; // Importação para o endpoint de período
import com.deliverytech.delivery_api.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    /**
     * GET /api/relatorios/vendas-por-restaurante
     * Vendas por restaurante (usando Projeção RelatorioVendasRestaurante)
     */
    @GetMapping("/vendas-por-restaurante")
    public ResponseEntity<List<RelatorioVendasRestaurante>> getVendasPorRestaurante() {
        List<RelatorioVendasRestaurante> relatorio = relatorioService.getVendasPorRestaurante();
        return ResponseEntity.ok(relatorio);
    }

    /**
     * GET /api/relatorios/produtos-mais-vendidos
     * Top produtos mais vendidos (usando Projeção RelatorioProdutosMaisVendidos)
     */
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<RelatorioProdutosMaisVendidos>> getProdutosMaisVendidos() {
        List<RelatorioProdutosMaisVendidos> relatorio = relatorioService.getProdutosMaisVendidos();
        return ResponseEntity.ok(relatorio);
    }

    /**
     * GET /api/relatorios/clientes-ativos
     * Ranking de clientes (usando Projeção RelatorioRankingClientes)
     */
    @GetMapping("/clientes-ativos")
    public ResponseEntity<List<RelatorioRankingClientes>> getRankingClientes() {
        List<RelatorioRankingClientes> relatorio = relatorioService.getRankingClientes();
        return ResponseEntity.ok(relatorio);
    }

    /**
     * GET /api/relatorios/pedidos-por-periodo
     * Pedidos criados em um determinado período
     * * NOTA: Este é o único endpoint que exige a criação do método correspondente no Service e Repository.
     */
    @GetMapping("/pedidos-por-periodo")
    public ResponseEntity<List<Pedido>> getPedidosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        // Assumindo que o Service retorna a lista completa de Pedidos (Entidade) para este relatório
        List<Pedido> pedidos = relatorioService.getPedidosPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(pedidos);
    }
}