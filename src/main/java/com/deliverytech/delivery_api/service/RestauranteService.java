package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.stereotype.Service;

@Service
public class RestauranteService {
    private final RestauranteRepository restauranteRepository;

    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public Restaurante cadastrar(Restaurante restaurante) {
        validarNomeUnico(restaurante.getNome());
        restaurante.setAtivo(true);
        return restauranteRepository.save(restaurante);
    }

    public Restaurante atualizar(Long id, Restaurante novosDados) {
        Restaurante existente = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));

        validarNomeUnico(novosDados.getNome(), id);

        existente.setNome(novosDados.getNome());
        existente.setCategoria(novosDados.getCategoria());
        return restauranteRepository.save(existente);
    }

    public Restaurante inativar(Long id) {
        Restaurante existente = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));

        existente.setAtivo(false);
        return restauranteRepository.save(existente);
    }

    // validação de nome único
    private void validarNomeUnico(String nome) {
        if(restauranteRepository.findByNomeContainingIgnoreCase(nome).stream().findAny().isPresent()){
            throw new RegraNegocioException("Já existe um restaurante com esse nome: " + nome);
        }
    }

    // validação para atualização (ignora o próprio restaurante)
    private void validarNomeUnico(String nome, Long idAtual) {
        boolean nomeJaExiste = restauranteRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .anyMatch(r -> !r.getId().equals(idAtual));
        if(nomeJaExiste) {
            throw new RegraNegocioException("Já existe um restaurante com esse nome: " + nome);
        }
    }
}

