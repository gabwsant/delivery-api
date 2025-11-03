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

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<ItemPedido> itens;

    @Column(name = "data_pedido")
    private LocalDateTime dataPedido;

    private BigDecimal total;

    private String status;

    public void atualizarStatus(String novoStatus) {
        this.status = novoStatus;
    }

    // Método helper para calcular o total
    public void calcularTotal() {
        if (this.itens == null) {
            this.total = BigDecimal.ZERO;
            return;
        }
        this.total = this.itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getNome() : "null") +
                ", restaurante=" + (restaurante != null ? restaurante.getNome() : "null") +
                ", dataPedido=" + dataPedido +
                ", total=" + total +
                ", status='" + status + '\'' +
                '}';
    }
}