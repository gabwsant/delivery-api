package com.deliverytech.delivery_api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoResponseDTO {
    private Long id;
    private Long clienteId;
    private String clienteNome;
    private Long restauranteId;
    private String restauranteNome;
    private BigDecimal total;
    private String status;
    private LocalDateTime dataPedido;
    private List<ItemPedidoResponseDTO> itens;
}