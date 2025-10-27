package com.deliverytech.delivery_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @ManyToMany
    @JoinTable(
            name = "pedido_produto",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    private List<Produto> produtos;

    @Column(name = "data_pedido")
    private LocalDateTime dataPedido;

    private BigDecimal total;

    private String status; // ex: "PENDENTE", "EM_PREPARO", "ENTREGUE"

    public void atualizarStatus(String novoStatus) {
        this.status = novoStatus;
    }

    public void calcularTotal() {
        if (produtos != null) {
            this.total = produtos.stream()
                    .map(Produto::getPreco)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}