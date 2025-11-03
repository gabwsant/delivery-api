package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- IMPORTAR

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          RestauranteRepository restauranteRepository) {
        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
    }

    // ... (métodos cadastrar, atualizar, validarPreco, buscarPorRestaurante, buscarPorId estão CORRETOS) ...
    public Produto cadastrar(Long restauranteId, Produto produto) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));

        if(!restaurante.isAtivo()) { // Boa prática: usar getter 'isAtivo' para boolean
            throw new RegraNegocioException("Não é possível adicionar produto a restaurante inativo");
        }

        validarPreco(produto.getPreco());
        produto.setRestaurante(restaurante);
        produto.setAtivo(true);

        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, Produto novosDados) {
        Produto existente = produtoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado"));

        validarPreco(novosDados.getPreco());

        existente.setNome(novosDados.getNome());
        existente.setDescricao(novosDados.getDescricao());
        existente.setPreco(novosDados.getPreco());
        existente.setAtivo(novosDados.isAtivo()); // Boa prática: usar getter 'isAtivo'

        return produtoRepository.save(existente);
    }

    // CORREÇÃO: Adicionada anotação @Transactional
    @Transactional
    public void alterarDisponibilidade(Long id, boolean ativo) {
        if (!produtoRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Produto não encontrado");
        }
        produtoRepository.setAtivo(id, ativo);
    }

    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O preço do produto não pode ser nulo ou menor/igual a zero.");
        }
    }

    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));

        return produtoRepository.findByRestauranteAndAtivoTrue(restaurante);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado"));
    }
}