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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // =================== CREATE ===================
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody PedidoRequestDTO dto) {

        // Converte a Lista de DTOs de Itens para o Map que o Service espera
        Map<Long, Integer> itensMap = dto.getItens().stream()
                .collect(Collectors.toMap(
                        ItemPedidoRequestDTO::getProdutoId,
                        ItemPedidoRequestDTO::getQuantidade
                ));

        Pedido pedido = pedidoService.criarPedido(
                dto.getClienteId(),
                dto.getRestauranteId(),
                itensMap // Envia o Map corrigido
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(pedido));
    }

    // =================== READ ===================
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.buscarPorCliente(clienteId);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // =================== UPDATE ===================
    @PutMapping("/{pedidoId}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(@PathVariable Long pedidoId,
                                                             @RequestParam String status) {
        Pedido atualizado = pedidoService.atualizarStatus(pedidoId, status);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    // =================== CONVERSOR ===================
    private PedidoResponseDTO toResponse(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setClienteId(pedido.getCliente().getId());
        dto.setClienteNome(pedido.getCliente().getNome());
        dto.setRestauranteId(pedido.getRestaurante().getId());
        dto.setRestauranteNome(pedido.getRestaurante().getNome());
        dto.setTotal(pedido.getTotal());
        dto.setStatus(pedido.getStatus());
        dto.setDataPedido(pedido.getDataPedido());

        // Mapeia a lista de Entidades ItemPedido para DTOs ItemPedidoResponse
        List<ItemPedidoResponseDTO> itensDto = pedido.getItens().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        dto.setItens(itensDto);

        return dto;
    }

    // Conversor auxiliar para o item
    private ItemPedidoResponseDTO mapItemToResponse(ItemPedido item) {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();
        dto.setNomeProduto(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}