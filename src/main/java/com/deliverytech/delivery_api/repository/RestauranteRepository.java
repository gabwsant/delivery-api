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

    // ----------------------------------------------------------------------
    // MÉTODOS EXISTENTES
    // ----------------------------------------------------------------------
    List<Restaurante> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT r FROM Restaurante r WHERE LOWER(r.categoria) = LOWER(:categoria)")
    List<Restaurante> findByCategoria(@Param("categoria") String categoria);

    List<Restaurante> findByAtivoTrue();

    @Query("SELECT r FROM Restaurante r WHERE r.ativo = true ORDER BY r.avaliacao DESC")
    List<Restaurante> findAtivosOrderByAvaliacao();

    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxa);

    List<Restaurante> findTop5ByOrderByNomeAsc();

    @Query("SELECT DISTINCT r FROM Restaurante r LEFT JOIN FETCH r.produtos")
    List<Restaurante> findAllWithProdutos();

    // ----------------------------------------------------------------------
    // NOVOS MÉTODOS OBRIGATÓRIOS PARA A SERVICE
    // ----------------------------------------------------------------------

    /**
     * NOVO MÉTODO: Suporta a busca por filtros dinâmicos (categoria e ativo).
     * Usamos JPQL com o operador LIKE e IS TRUE/FALSE para tratar a optionalidade
     * e o caso de todos os restaurantes.
     *
     * @param categoria Filtro de categoria (pode ser null)
     * @param ativo Filtro de status (pode ser null)
     * @return Lista de restaurantes filtrados
     */
    @Query("SELECT r FROM Restaurante r WHERE " +
            // Se 'categoria' for nulo, a condição é sempre verdadeira (OR :categoria IS NULL)
            "(:categoria IS NULL OR LOWER(r.categoria) LIKE LOWER(CONCAT('%', :categoria, '%'))) AND " +
            // Se 'ativo' for nulo, a condição é sempre verdadeira (OR :ativo IS NULL)
            "(:ativo IS NULL OR r.ativo = :ativo)")
    List<Restaurante> listarPorFiltros(
            @Param("categoria") String categoria,
            @Param("ativo") Boolean ativo);

    /**
     * NOVO MÉTODO: Simulação de busca por proximidade via CEP.
     * NOTE: A implementação real dessa funcionalidade é altamente dependente
     * de bibliotecas de geolocalização (e.g., PostGIS, MySQL Spatial) e da
     * estrutura da entidade Restaurante (que precisaria de campos de latitude/longitude).
     *
     * Este é um placeholder. A service irá retornar todos os restaurantes até que
     * a lógica de geolocalização seja implementada.
     */
    // [Image of database query flow for finding nearest restaurants using geo-coordinates]
    List<Restaurante> findProximos(String cep); // Você deve substituir este método por uma query Geo-Espacial no futuro.
}