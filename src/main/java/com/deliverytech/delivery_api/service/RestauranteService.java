package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    // =================== CREATE ===================
    public Restaurante cadastrar(Restaurante restaurante) {
        validarNomeUnico(restaurante.getNome());
        restaurante.setAtivo(true);
        return restauranteRepository.save(restaurante);
    }

    // =================== READ ===================
    public Restaurante buscarPorId(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));
    }

    public List<Restaurante> listarTodos() {
        return restauranteRepository.findAll();
    }

    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria);
    }

    // =================== UPDATE ===================
    public Restaurante atualizar(Long id, Restaurante novosDados) {
        Restaurante existente = buscarPorId(id);

        validarNomeUnico(novosDados.getNome(), id);

        existente.setNome(novosDados.getNome());
        existente.setCategoria(novosDados.getCategoria());
        existente.setTelefone(novosDados.getTelefone());
        existente.setEndereco(novosDados.getEndereco());
        existente.setCnpj(novosDados.getCnpj());

        return restauranteRepository.save(existente);
    }

    // =================== DELETE ===================
    public void deletar(Long id) {
        Restaurante existente = buscarPorId(id);
        restauranteRepository.delete(existente);
    }

    // =================== INATIVAR ===================
    public Restaurante inativar(Long id) {
        Restaurante existente = buscarPorId(id);
        existente.setAtivo(false);
        return restauranteRepository.save(existente);
    }

    // =================== VALIDAÇÃO ===================
    private void validarNomeUnico(String nome) {
        if (restauranteRepository.findByNomeContainingIgnoreCase(nome).stream().findAny().isPresent()) {
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
