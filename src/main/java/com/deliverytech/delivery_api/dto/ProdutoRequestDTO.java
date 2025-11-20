package com.deliverytech.delivery_api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequestDTO {

    // Campo obrigatório para o cadastro do produto
    private Long restauranteId;

    private String nome;
    private String descricao;
    private BigDecimal preco;

    // Opcional para update, mas pode ser incluído no cadastro
    private Boolean ativo;
}