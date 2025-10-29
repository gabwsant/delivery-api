package com.deliverytech.delivery_api.dto;

import lombok.Data;

@Data
public class ProdutoRequestDTO {
    public String nome;
    public String descricao;
    public double preco;
}
