package com.deliverytech.delivery_api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequestDTO {
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Boolean ativo; // Opcional para update
}
