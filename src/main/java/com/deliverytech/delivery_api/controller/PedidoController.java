package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.PedidoRequestDTO;
import com.deliverytech.delivery_api.dto.PedidoResponseDTO;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        Pedido pedido = pedidoService.criarPedido(dto.getClienteId(),
                dto.getRestauranteId(),
                dto.getProdutosIds());
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
        dto.setProdutosNomes(pedido.getProdutos()
                .stream()
                .map(Produto::getNome)
                .collect(Collectors.toList()));
        dto.setTotal(pedido.getTotal());
        dto.setStatus(pedido.getStatus());
        dto.setDataPedido(pedido.getDataPedido());
        return dto;
    }
}
