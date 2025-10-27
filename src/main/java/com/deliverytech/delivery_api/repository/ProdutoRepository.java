package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Buscar produtos de um restaurante específico
    List<Produto> findByRestaurante(Restaurante restaurante);

    // Buscar por categoria (se o campo existir)
    @Query("SELECT p FROM Produto p WHERE LOWER(p.descricao) LIKE LOWER(CONCAT('%', :categoria, '%'))")
    List<Produto> findByCategoriaAproximada(@Param("categoria") String categoria);

    // Buscar produtos disponíveis (ativos)
    List<Produto> findByAtivoTrue();

    // Buscar por restaurante e disponibilidade
    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.ativo = true")
    List<Produto> findDisponiveisByRestaurante(@Param("restauranteId") Long restauranteId);
}
