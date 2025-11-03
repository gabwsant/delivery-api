package com.deliverytech.delivery_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
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

    @Column(nullable = false)
    private boolean ativo = true;

    private BigDecimal taxaEntrega;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Produto> produtos;

    public void inativar() {
        this.ativo = false;
    }

    @Override
    public String toString() {
        return "Restaurante{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}