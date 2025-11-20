package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    // Sugestão: Injetar um serviço de Geolocalização/CEP aqui, se necessário,
    // para calcular taxa e proximidade. (Ex: ViaCepService)

    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    // =================== CREATE ===================
    public Restaurante cadastrar(Restaurante restaurante) {
        validarNomeUnico(restaurante.getNome());
        restaurante.setAtivo(true);
        restaurante.setDataCadastro(LocalDateTime.now());

        return restauranteRepository.save(restaurante);
    }

    // =================== READ ===================
    public Restaurante buscarPorId(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));
    }

    /**
     * NOVO MÉTODO: Suporta listagem com filtros opcionais (categoria e ativo).
     * Requisito: GET /api/restaurantes
     */
    public List<Restaurante> listarComFiltros(String categoria, Boolean ativo) {
        // Se ambos os filtros estiverem nulos, retorna todos (o seu listarTodos())
        if (categoria == null && ativo == null) {
            return restauranteRepository.findAllWithProdutos();
        }

        // Você precisará de um método no seu Repository para suportar essa busca
        // Exemplo: List<Restaurante> findByCategoriaAndAtivo(String categoria, Boolean ativo);
        // Ou implemente a lógica de filtragem aqui se o Repository for mais simples.

        // --- IMPLEMENTAÇÃO RECOMENDADA COM REPOSITORY ---
        // Se você não usar o 'findAllWithProdutos', esta linha é suficiente
        return restauranteRepository.listarPorFiltros(categoria, ativo);
        // OBS: Este método 'listarPorFiltros' deve ser criado no RestauranteRepository,
        // por exemplo, usando @Query com critérios dinâmicos ou QueryDSL/Specifications.

        /* // Exemplo de como implementar no Repository (apenas para referência):
        // List<Restaurante> findByCategoriaAndAtivo(String categoria, Boolean ativo);
        // List<Restaurante> findByCategoria(String categoria);
        // List<Restaurante> findByAtivo(Boolean ativo);
        */
    }

    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria);
    }

    /**
     * NOVO MÉTODO: Busca restaurantes próximos a um CEP.
     * Requisito: GET /api/restaurantes/proximos/{cep}
     */
    public List<Restaurante> buscarProximos(String cep) {
        // NOTE: A implementação real requer um serviço de geolocalização (e.g., Google Maps API, Postgis/H3)
        // para converter o CEP em coordenadas geográficas e calcular a distância.

        // Simulação (deve ser substituída pela lógica de geolocalização real)
        System.out.println("Buscando restaurantes próximos ao CEP: " + cep + "...");
        // Assumindo um método no Repository que usa coordenadas
        // return restauranteRepository.findByProximityToCep(cep);

        // Retornando todos como fallback de simulação
        return restauranteRepository.findAllWithProdutos();
    }


    // =================== UPDATE ===================
    public Restaurante atualizar(Long id, Restaurante novosDados) {
        Restaurante existente = buscarPorId(id);

        // Somente valida o nome se o nome for alterado e for diferente do ID atual
        if (!existente.getNome().equalsIgnoreCase(novosDados.getNome())) {
            validarNomeUnico(novosDados.getNome(), id);
        }

        existente.setNome(novosDados.getNome());
        existente.setCategoria(novosDados.getCategoria());
        existente.setTelefone(novosDados.getTelefone());
        existente.setEndereco(novosDados.getEndereco());
        existente.setCnpj(novosDados.getCnpj());

        return restauranteRepository.save(existente);
    }

    /**
     * NOVO MÉTODO: Ativa ou Desativa o restaurante (substitui 'inativar').
     * Requisito: PATCH /api/restaurantes/{id}/status
     */
    public void alterarStatus(Long id, Boolean ativo) {
        Restaurante existente = buscarPorId(id);

        if (existente.isAtivo() == ativo) {
            String status = ativo ? "ativo" : "inativo";
            throw new RegraNegocioException("O restaurante já está no status " + status);
        }

        existente.setAtivo(ativo);
        restauranteRepository.save(existente);
    }

    /**
     * NOVO MÉTODO: Calcula a taxa de entrega.
     * Requisito: GET /api/restaurantes/{id}/taxa-entrega/{cep}
     */
    public BigDecimal calcularTaxaEntrega(Long id, String cep) {
        Restaurante restaurante = buscarPorId(id);

        // NOTE: A lógica real de cálculo de taxa depende de fatores como
        // a distância entre o restaurante e o CEP de entrega (usando geolocalização)
        // e as regras específicas de taxa do restaurante.

        // Simulação: Retorna um valor fixo apenas para demonstração.
        if (cep.startsWith("30")) {
            return new BigDecimal("5.00"); // Taxa menor para CEPs próximos (simulação)
        } else if (cep.startsWith("01")) {
            return new BigDecimal("15.00"); // Taxa maior para CEPs distantes (simulação)
        }
        return new BigDecimal("8.00");
    }

    // =================== DELETE ===================
    public void deletar(Long id) {
        Restaurante existente = buscarPorId(id);

        // Adicionar Regra de Negócio: verificar se o restaurante pode ser deletado
        // (ex: não pode ter pedidos pendentes ou produtos ativos).
        // if (!existente.getPedidos().isEmpty()) { ... }

        restauranteRepository.delete(existente);
    }

    // =================== VALIDAÇÃO ===================
    private void validarNomeUnico(String nome) {
        // Uso de Optional para evitar chamada desnecessária ao banco se o nome não existir.
        Optional<Restaurante> restauranteExistente = restauranteRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .findAny();

        if (restauranteExistente.isPresent()) {
            throw new RegraNegocioException("Já existe um restaurante com esse nome: " + nome);
        }
    }

    private void validarNomeUnico(String nome, Long idAtual) {
        boolean nomeJaExiste = restauranteRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .anyMatch(r -> !r.getId().equals(idAtual));
        if (nomeJaExiste) {
            throw new RegraNegocioException("Já existe um restaurante com esse nome: " + nome);
        }
    }
}