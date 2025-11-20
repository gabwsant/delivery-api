package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ItemPedidoRequestDTO;
import com.deliverytech.delivery_api.dto.ItemPedidoResponseDTO;
import com.deliverytech.delivery_api.dto.PedidoRequestDTO;
import com.deliverytech.delivery_api.dto.PedidoResponseDTO;
import com.deliverytech.delivery_api.entity.ItemPedido;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal; // Importação necessária
import java.time.LocalDateTime; // Importação necessária
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
// Ajustado para o path '/api/pedidos' conforme o requisito
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // =================== CREATE ===================
    /**
     * POST /api/pedidos - Criar pedido
     */
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody PedidoRequestDTO dto) {

        // Conversão de DTO de Itens para o Map que o Service espera
        Map<Long, Integer> itensMap = dto.getItens().stream()
                .collect(Collectors.toMap(
                        ItemPedidoRequestDTO::getProdutoId,
                        ItemPedidoRequestDTO::getQuantidade
                ));

        Pedido pedido = pedidoService.criarPedido(
                dto.getClienteId(),
                dto.getRestauranteId(),
                itensMap
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(pedido));
    }

    /**
     * POST /api/pedidos/calcular - Calcular total sem salvar
     * NOVO ENDPOINT
     */
    @PostMapping("/calcular")
    public ResponseEntity<BigDecimal> calcularTotal(@RequestBody PedidoRequestDTO dto) {

        Map<Long, Integer> itensMap = dto.getItens().stream()
                .collect(Collectors.toMap(
                        ItemPedidoRequestDTO::getProdutoId,
                        ItemPedidoRequestDTO::getQuantidade
                ));

        BigDecimal total = pedidoService.calcularTotal(
                dto.getRestauranteId(), // Pode ser necessário para taxa de entrega
                itensMap
        );

        return ResponseEntity.ok(total);
    }


    // =================== READ ===================
    /**
     * GET /api/pedidos/{id} - Buscar pedido completo
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorIdCompleto(id);
        return ResponseEntity.ok(toResponse(pedido));
    }

    /**
     * GET /api/pedidos - Listar com filtros (status, data)
     * Adicionado para filtros dinâmicos
     */
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarComFiltros(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        List<Pedido> pedidos = pedidoService.listarComFiltros(status, dataInicio, dataFim);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/clientes/{clienteId}/pedidos - Histórico do cliente
     * NOTA: Mapeamento em /api/pedidos/cliente/{clienteId}
     */
    @GetMapping("/clientes/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        // Método já existente
        List<Pedido> pedidos = pedidoService.buscarPorCliente(clienteId);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/restaurantes/{restauranteId}/pedidos - Pedidos do restaurante
     * NOVO ENDPOINT
     */
    @GetMapping("/restaurantes/{restauranteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorRestaurante(@PathVariable Long restauranteId) {
        List<Pedido> pedidos = pedidoService.buscarPorRestaurante(restauranteId);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // =================== UPDATE ===================
    /**
     * PATCH /api/pedidos/{id}/status - Atualizar status
     * Uso do PATCH e Path correto
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(@PathVariable Long id,
                                                             @RequestParam String status) {
        Pedido atualizado = pedidoService.atualizarStatus(id, status);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    // =================== DELETE ===================
    /**
     * DELETE /api/pedidos/{id} - Cancelar pedido
     * Adicionado o método de serviço `cancelarPedido`
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    // =================== CONVERSOR ===================
    // Métodos toResponse e mapItemToResponse permanecem os mesmos
    private PedidoResponseDTO toResponse(Pedido pedido) {
        // ... (lógica de conversão DTO) ...
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setClienteId(pedido.getCliente().getId());
        dto.setClienteNome(pedido.getCliente().getNome());
        dto.setRestauranteId(pedido.getRestaurante().getId());
        dto.setRestauranteNome(pedido.getRestaurante().getNome());
        dto.setTotal(pedido.getTotal());
        dto.setStatus(pedido.getStatus());
        dto.setDataPedido(pedido.getDataPedido());

        List<ItemPedidoResponseDTO> itensDto = pedido.getItens().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        dto.setItens(itensDto);

        return dto;
    }

    private ItemPedidoResponseDTO mapItemToResponse(ItemPedido item) {
        // ... (lógica de conversão DTO) ...
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();
        dto.setNomeProduto(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}