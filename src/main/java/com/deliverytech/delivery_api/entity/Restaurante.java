package com.deliverytech.delivery_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurantes")
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cnpj;

    private String endereco;

    private String telefone;

    private String categoria;

    private Double avaliacao;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(nullable = true)
    private Boolean ativo;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL)
    private List<Produto> produtos;

    public void inativar() {
        this.ativo = false;
    }
}
