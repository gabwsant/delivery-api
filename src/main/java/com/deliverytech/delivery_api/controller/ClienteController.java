package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ClienteRequestDTO;
import com.deliverytech.delivery_api.dto.ClienteResponseDTO;
import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // POST /clientes → cadastrar
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(@RequestBody ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        // Corrigido para usar getters
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());

        // Removido: Lógica de negócio (setAtivo, setDataCadastro)
        // O Service é responsável por isso.

        Cliente salvo = clienteService.cadastrar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(salvo));
    }

    // GET /clientes → listar ativos
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        List<Cliente> clientes = clienteService.buscarTodosAtivos();
        List<ClienteResponseDTO> response = clientes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /clientes/{id} → buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(mapToResponse(cliente));
    }

    // PUT /clientes/{id} → atualizar
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody ClienteRequestDTO dto) {
        Cliente novosDados = new Cliente();
        novosDados.setNome(dto.getNome());
        novosDados.setEmail(dto.getEmail());
        novosDados.setTelefone(dto.getTelefone());
        novosDados.setEndereco(dto.getEndereco());

        Cliente atualizado = clienteService.atualizar(id, novosDados);
        return ResponseEntity.ok(mapToResponse(atualizado));
    }

    // DELETE /clientes/{id} → inativar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        clienteService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------
    // Conversão entidade → DTO
    // --------------------
    private ClienteResponseDTO mapToResponse(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        // Corrigido para usar setters
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setEndereco(cliente.getEndereco());
        dto.setAtivo(cliente.isAtivo()); // Corrigido para 'isAtivo'
        dto.setDataCadastro(cliente.getDataCadastro());
        return dto;
    }
}