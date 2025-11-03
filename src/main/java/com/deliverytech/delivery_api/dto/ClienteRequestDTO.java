package com.deliverytech.delivery_api.dto;

import lombok.Data;

@Data
public class ClienteRequestDTO {
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
}