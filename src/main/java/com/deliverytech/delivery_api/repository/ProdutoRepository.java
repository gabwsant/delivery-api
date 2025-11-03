package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByRestaurante(Restaurante restaurante);

    @Query("SELECT p FROM Produto p WHERE LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Produto> findByDescricaoContendo(@Param("termo") String termo);

    List<Produto> findByAtivoTrue();

    List<Produto> findByRestauranteId(Long restauranteId);

    List<Produto> findByRestauranteCategoria(String categoria);

    List<Produto> findByPrecoLessThanEqual(BigDecimal preco);

    List<Produto> findByRestauranteAndAtivoTrue(Restaurante restaurante);

    @Modifying
    @Query("UPDATE Produto p SET p.ativo = :ativo WHERE p.id = :id")
    void setAtivo(@Param("id") Long id, @Param("ativo") boolean ativo);
}