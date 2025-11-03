package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    // Buscar por nome (contendo parte do nome)
    List<Restaurante> findByNomeContainingIgnoreCase(String nome);

    // Buscar por categoria (se tiver esse campo)
    @Query("SELECT r FROM Restaurante r WHERE LOWER(r.categoria) = LOWER(:categoria)")
    List<Restaurante> findByCategoria(@Param("categoria") String categoria);

    // Buscar apenas os restaurantes ativos
    List<Restaurante> findByAtivoTrue();

    // Buscar por ativos e ordenar por avaliação (descendente)
    @Query("SELECT r FROM Restaurante r WHERE r.ativo = true ORDER BY r.avaliacao DESC")
    List<Restaurante> findAtivosOrderByAvaliacao();

    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxa);

    List<Restaurante> findTop5ByOrderByNomeAsc();

    @Query("SELECT DISTINCT r FROM Restaurante r LEFT JOIN FETCH r.produtos")
    List<Restaurante> findAllWithProdutos();
}
