package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Resposta detalhada contendo informações de um produto.")
public class ProdutoResponseDTO {

    @Schema(description = "ID único gerado pelo sistema", example = "10")
    private Long id;

    @Schema(description = "Nome do produto", example = "Pizza Calabresa Grande")
    private String nome;

    @Schema(description = "Descrição detalhada do produto", example = "Calabresa, muçarela, azeitonas e cebola.")
    private String descricao;

    @Schema(description = "Preço unitário do produto", example = "55.90", type = "number", format = "double")
    private BigDecimal preco;

    @Schema(description = "Indica se o produto está disponível para venda", example = "true")
    private Boolean ativo;

    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1")
    private Long restauranteId;

    @Schema(description = "Nome do restaurante", example = "Cantinho da Pizza")
    private String restauranteNome;
}