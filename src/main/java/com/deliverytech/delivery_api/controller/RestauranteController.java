package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.service.RestauranteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    // =================== CREATE ===================
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@RequestBody RestauranteRequestDTO dto) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.getNome());
        restaurante.setCnpj(dto.getCnpj());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setAtivo(true);
        restaurante.setDataCadastro(LocalDateTime.now());
        restaurante.setProdutos(null);

        Restaurante salvo = restauranteService.cadastrar(restaurante);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvo));
    }

    // =================== READ ===================
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
        Restaurante restaurante = restauranteService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(restaurante));
    }

    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listarTodos() {
        List<Restaurante> restaurantes = restauranteService.listarTodos();
        List<RestauranteResponseDTO> dtos = restaurantes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        List<Restaurante> restaurantes = restauranteService.buscarPorCategoria(categoria);
        List<RestauranteResponseDTO> dtos = restaurantes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // =================== UPDATE ===================
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizar(@PathVariable Long id,
                                                            @RequestBody RestauranteRequestDTO dto) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.getNome());
        restaurante.setCnpj(dto.getCnpj());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setCategoria(dto.getCategoria());

        Restaurante atualizado = restauranteService.atualizar(id, restaurante);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    // =================== DELETE ===================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        restauranteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

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
        dto.setAtivo(restaurante.getAtivo());
        return dto;
    }
}
