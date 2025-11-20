package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
// Ajustado para o path '/api/produtos'
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // =================== CREATE ===================
    /**
     * POST /api/produtos - Cadastrar produto
     * O ID do restaurante deve vir no DTO.
     */
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@RequestBody ProdutoRequestDTO dto) {
        // Validação: Restaurante ID é obrigatório para cadastrar
        if (dto.getRestauranteId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());

        // No Service, vamos precisar receber o ID do restaurante
        Produto salvo = produtoService.cadastrar(dto.getRestauranteId(), produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvo));
    }

    // =================== READ ===================
    /**
     * GET /api/produtos/{id} - Buscar por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(produto));
    }

    /**
     * GET /api/restaurantes/{restauranteId}/produtos - Produtos do restaurante
     * NOTA: Este endpoint deve ser mapeado no RestauranteController ou ter um RequestMapping específico.
     * Como o requisito colocou este path na lista de ProdutoController, vamos mapeá-lo aqui
     * com o path completo, assumindo que ele não está no RestauranteController.
     */
    @GetMapping("/restaurantes/{restauranteId}/produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarPorRestaurante(restauranteId);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/produtos/categoria/{categoria} - Por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/produtos/buscar?nome={nome} - Busca por nome
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        List<Produto> produtos = produtoService.buscarPorNome(nome);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // =================== UPDATE ===================
    /**
     * PUT /api/produtos/{id} - Atualizar produto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setAtivo(dto.getAtivo());

        Produto atualizado = produtoService.atualizar(id, produto);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    /**
     * PATCH /api/produtos/{id}/disponibilidade - Toggle disponibilidade
     * O corpo da requisição pode ser um simples JSON com o novo status: {"ativo": true}
     */
    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<Void> alterarDisponibilidade(@PathVariable Long id, @RequestBody Boolean ativo) {
        produtoService.alterarDisponibilidade(id, ativo);
        return ResponseEntity.noContent().build();
    }

    // =================== DELETE ===================
    /**
     * DELETE /api/produtos/{id} - Remover produto (Assumindo que deletar é remover o registro)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Se a intenção é remover o registro, use o método deletar no Service.
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // =================== CONVERSOR ===================
    private ProdutoResponseDTO toResponse(Produto produto) {
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