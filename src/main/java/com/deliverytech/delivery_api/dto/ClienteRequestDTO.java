package com.deliverytech.delivery_api.dto;

import lombok.Data;

@Data
public class ClienteRequestDTO {

    public String nome;
    public String email;
    public String telefone;
    public String endereco;
}
