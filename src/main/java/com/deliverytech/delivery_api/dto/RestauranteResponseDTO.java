package com.deliverytech.delivery_api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RestauranteResponseDTO {
    private Long id;
    private String nome;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String categoria;
    private Double avaliacao;
    private LocalDateTime dataCadastro;
    private Boolean ativo;
    private List<ProdutoResponseDTO> produtos;
}
