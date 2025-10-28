package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrar(Cliente cliente) {
        emailExiste(cliente.getEmail());
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());
        clienteRepository.save(cliente);
        return cliente;
    }

    public Cliente atualizar(Long id, Cliente novosDados) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));
        validarEmailUnico(novosDados.getEmail(), id);

        clienteExistente.setNome(novosDados.getNome());
        clienteExistente.setTelefone(novosDados.getTelefone());
        clienteExistente.setEndereco(novosDados.getEndereco());
        clienteExistente.setEmail(novosDados.getEmail());
        return clienteRepository.save(clienteExistente);
    }

    public Cliente inativar(Long id) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));

        clienteExistente.inativar();
        return clienteRepository.save(clienteExistente);
    }

    private void emailExiste(String email) {
        if(clienteRepository.existsByEmail(email)){
            throw new RegraNegocioException("E-mail já cadastrado: " + email);
        }
    }

    private void validarEmailUnico(String email, Long idAtual) {
        boolean emailJaExiste = clienteRepository.findByEmail(email)
                .filter(c -> !c.getId().equals(idAtual)) // ignora o próprio cliente
                .isPresent();

        if (emailJaExiste) {
            throw new RegraNegocioException("E-mail já cadastrado: " + email);
        }
    }


}
