package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
// 2.3: @Schema - Documentar DTOs
@Schema(name = "RestauranteRequest", description = "Dados de entrada para a criação ou atualização de um restaurante.")
public class RestauranteRequestDTO {

    @Schema(description = "Nome fantasia do restaurante", example = "Cantinho da Pizza")
    @NotBlank
    private String nome;

    @Schema(description = "CNPJ do restaurante (apenas números)", example = "12345678000190", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 14, max = 14)
    private String cnpj;

    @Schema(description = "Endereço completo da sede principal", example = "Rua das Flores, 100 - Centro")
    private String endereco;

    @Schema(description = "Telefone de contato", example = "(31) 99999-0000")
    private String telefone;

    @Schema(description = "Tipo de culinária do restaurante", example = "Pizzaria", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoria;
}