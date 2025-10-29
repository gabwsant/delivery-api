package com.deliverytech.delivery_api.dto;

import lombok.Data;

@Data
public class RestauranteRequestDTO {
    private String nome;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String categoria;
}
