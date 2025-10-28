package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Cadastrar cliente
    public Cliente cadastrar(Cliente cliente) {
        validarCampos(cliente);
        validarEmailUnico(cliente.getEmail(), null);

        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    // Atualizar cliente
    public Cliente atualizar(Long id, Cliente novosDados) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));

        validarCampos(novosDados);
        validarEmailUnico(novosDados.getEmail(), id);

        clienteExistente.setNome(novosDados.getNome());
        clienteExistente.setTelefone(novosDados.getTelefone());
        clienteExistente.setEndereco(novosDados.getEndereco());
        clienteExistente.setEmail(novosDados.getEmail());

        return clienteRepository.save(clienteExistente);
    }

    // Inativar cliente
    public Cliente inativar(Long id) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));

        clienteExistente.inativar();
        return clienteRepository.save(clienteExistente);
    }

    // Buscar todos ativos
    public List<Cliente> buscarTodosAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    // Buscar por ID
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));
    }

    // -------------------
    // Validações internas
    // -------------------
    private void validarCampos(Cliente cliente) {
        if(cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new RegraNegocioException("Nome é obrigatório");
        }
        if(cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new RegraNegocioException("E-mail é obrigatório");
        }
        if(!cliente.getEmail().contains("@")) {
            throw new RegraNegocioException("E-mail inválido");
        }
    }

    private void validarEmailUnico(String email, Long idAtual) {
        boolean emailJaExiste = clienteRepository.findByEmail(email)
                .filter(c -> !c.getId().equals(idAtual))
                .isPresent();

        if (emailJaExiste) {
            throw new RegraNegocioException("E-mail já cadastrado: " + email);
        }
    }
}
