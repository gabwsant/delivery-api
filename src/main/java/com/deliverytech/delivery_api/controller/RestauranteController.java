package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.dto.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal; // Necessário para a taxa de entrega
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Restaurantes", description = "Gerenciamento de Restaurantes e suas operações")
@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    // =================== CREATE ===================
    /**
     * POST /api/restaurantes - Cadastrar restaurante
     */
    @Operation(summary = "Cadastra um novo restaurante", description = "Cria um novo registro de restaurante na base de dados.")
    @ApiResponse(responseCode = "201", description = "Restaurante cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (CNPJ ou nome duplicado)")
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@RequestBody RestauranteRequestDTO dto) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.getNome());
        restaurante.setCnpj(dto.getCnpj());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setCategoria(dto.getCategoria());

        Restaurante salvo = restauranteService.cadastrar(restaurante);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvo));
    }

    // =================== READ ===================
    /**
     * GET /api/restaurantes - Listar com filtros (categoria, ativo)
     * Modificado para aceitar filtros como @RequestParam
     */
    @Operation(summary = "Lista restaurantes com filtros opcionais")
    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listarComFiltros(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo) {

        List<Restaurante> restaurantes = restauranteService.listarComFiltros(categoria, ativo);

        List<RestauranteResponseDTO> dtos = restaurantes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/restaurantes/{id} - Buscar por ID
     */
    @Operation(summary = "Busca um restaurante por ID")
    @ApiResponse(responseCode = "200", description = "Retorna o restaurante encontrado")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado para o ID informado")
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
        Restaurante restaurante = restauranteService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(restaurante));
    }

    /**
     * GET /api/restaurantes/categoria/{categoria} - Por categoria
     * Mantido o endpoint original
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        List<Restaurante> restaurantes = restauranteService.buscarPorCategoria(categoria);
        List<RestauranteResponseDTO> dtos = restaurantes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa
     * NOVO ENDPOINT
     */
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxaEntrega(
            @PathVariable Long id,
            @PathVariable String cep) {

        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(taxa);
    }

    /**
     * GET /api/restaurantes/proximos/{cep} - Restaurantes próximos
     * NOVO ENDPOINT
     */
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarProximos(@PathVariable String cep) {
        List<Restaurante> restaurantes = restauranteService.buscarProximos(cep);
        List<RestauranteResponseDTO> dtos = restaurantes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    // =================== UPDATE ===================
    /**
     * PUT /api/restaurantes/{id} - Atualizar restaurante
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizar(@PathVariable Long id,
                                                            @RequestBody RestauranteRequestDTO dto) {
        // No PUT, geralmente você popula o objeto com os dados da requisição,
        // mas o Service deve se encarregar de buscar o existente,
        // aplicar as mudanças e salvar.
        Restaurante restauranteAtualizado = new Restaurante();
        restauranteAtualizado.setNome(dto.getNome());
        restauranteAtualizado.setCnpj(dto.getCnpj());
        restauranteAtualizado.setEndereco(dto.getEndereco());
        restauranteAtualizado.setTelefone(dto.getTelefone());
        restauranteAtualizado.setCategoria(dto.getCategoria());

        Restaurante atualizado = restauranteService.atualizar(id, restauranteAtualizado);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    /**
     * PATCH /api/restaurantes/{id}/status - Ativar/desativar
     * NOVO ENDPOINT
     * O corpo da requisição pode ser um simples JSON com o novo status: {"ativo": true}
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id, @RequestBody Boolean ativo) {
        restauranteService.alterarStatus(id, ativo);
        return ResponseEntity.noContent().build();
    }

    // O endpoint DELETE /api/restaurantes/{id} foi removido pois não é obrigatório.

    // =================== CONVERSOR ===================
    public RestauranteResponseDTO toResponse(Restaurante restaurante) {
        RestauranteResponseDTO dto = new RestauranteResponseDTO();
        dto.setId(restaurante.getId());
        dto.setNome(restaurante.getNome());
        dto.setCnpj(restaurante.getCnpj());
        dto.setEndereco(restaurante.getEndereco());
        dto.setTelefone(restaurante.getTelefone());
        dto.setCategoria(restaurante.getCategoria());
        dto.setAvaliacao(restaurante.getAvaliacao());
        dto.setDataCadastro(restaurante.getDataCadastro());
        dto.setAtivo(restaurante.isAtivo());

        // Mapeamento da lista de produtos
        List<ProdutoResponseDTO> produtosDto = restaurante.getProdutos() != null ?
                restaurante.getProdutos().stream()
                        .map(this::mapProdutoToResponse)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        dto.setProdutos(produtosDto);

        return dto;
    }

    // Conversor auxiliar de Produto
    private ProdutoResponseDTO mapProdutoToResponse(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setAtivo(produto.isAtivo());
        dto.setRestauranteId(produto.getRestaurante().getId());
        dto.setRestauranteNome(produto.getRestaurante().getNome());
        return dto;
    }
}