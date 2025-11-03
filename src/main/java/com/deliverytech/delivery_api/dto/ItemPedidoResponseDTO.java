package com.deliverytech.delivery_api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemPedidoResponseDTO {
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;
}