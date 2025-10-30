package com.deliverytech.delivery_api.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    private Long clienteId;
    private Long restauranteId;
    private List<Long> produtosIds;
}
