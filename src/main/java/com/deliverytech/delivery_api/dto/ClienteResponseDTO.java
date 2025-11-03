package com.deliverytech.delivery_api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClienteResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private Boolean ativo;
    private LocalDateTime dataCadastro;
}