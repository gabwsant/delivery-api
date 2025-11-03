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
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // =================== CREATE ===================
    @PostMapping("/restaurante/{restauranteId}")
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@PathVariable Long restauranteId,
                                                        @RequestBody ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());

        // Se o DTO de request tem o campo 'ativo', podemos passá-lo
        // O Service (cadastrar) deve ser inteligente para usar isso ou setar um default
        if(dto.getAtivo() != null) {
            produto.setAtivo(dto.getAtivo());
        }
        // Se for nulo, o Service (ou a entidade) deve assumir um padrão (ex: true)

        Produto salvo = produtoService.cadastrar(restauranteId, produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvo));
    }

    // =================== READ ===================
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(produto));
    }

    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarPorRestaurante(restauranteId);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // =================== UPDATE ===================
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setAtivo(dto.getAtivo()); // No update, o service deve aceitar o que vem

        Produto atualizado = produtoService.atualizar(id, produto);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    // =================== DELETE (ou inativar) ===================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.alterarDisponibilidade(id, false);
        return ResponseEntity.noContent().build();
    }

    // =================== CONVERSOR ===================
    private ProdutoResponseDTO toResponse(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setAtivo(produto.isAtivo()); // Corrigido para 'isAtivo'
        dto.setRestauranteId(produto.getRestaurante().getId());
        dto.setRestauranteNome(produto.getRestaurante().getNome());
        return dto;
    }
}