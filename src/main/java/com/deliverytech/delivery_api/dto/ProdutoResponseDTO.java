package com.deliverytech.delivery_api.dto;

import lombok.Data;

@Data
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private double preco;
    private Boolean ativo;
    private Long restauranteId;
    private String restauranteNome;
}
