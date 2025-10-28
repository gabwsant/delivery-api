package com.deliverytech.delivery_api.dto;

import java.time.LocalDateTime;

public class ClienteResponseDTO {

    public Long id;
    public String nome;
    public String email;
    public String telefone;
    public String endereco;
    public Boolean ativo;
    public LocalDateTime dataCadastro;
}
