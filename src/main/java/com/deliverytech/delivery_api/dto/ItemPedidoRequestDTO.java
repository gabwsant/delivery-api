package com.deliverytech.delivery_api.dto;

import lombok.Data;

@Data
public class ItemPedidoRequestDTO {
    private Long produtoId;
    private Integer quantidade;
}