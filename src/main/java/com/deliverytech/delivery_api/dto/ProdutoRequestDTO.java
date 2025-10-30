package com.deliverytech.delivery_api.dto;

import lombok.Data;

@Data
public class ProdutoRequestDTO {
    private String nome;
    private String descricao;
    private double preco;
    private Boolean ativo; // Opcional para update
}
