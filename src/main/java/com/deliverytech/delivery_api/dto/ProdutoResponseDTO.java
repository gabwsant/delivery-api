package com.deliverytech.delivery_api.dto;

import com.deliverytech.delivery_api.entity.Restaurante;
import lombok.Data;

@Data
public class ProdutoResponseDTO {
    public Long id;
    public String nome;
    public String descricao;
    public double preco;
    public Restaurante restaurante;
    public Boolean ativo;
}
