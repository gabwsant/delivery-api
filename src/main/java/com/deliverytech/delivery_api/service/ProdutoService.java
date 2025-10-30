package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.stereotype.Service;

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

    public Produto cadastrar(Long restauranteId, Produto produto) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));

        if(!restaurante.getAtivo()) {
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
        existente.setAtivo(novosDados.getAtivo());

        return produtoRepository.save(existente);
    }

    public Produto alterarDisponibilidade(Long id, boolean ativo) {
        Produto existente = produtoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado"));

        existente.setAtivo(ativo);
        return produtoRepository.save(existente);
    }

    private void validarPreco(Double preco) {
        if(preco == null || preco <= 0) {
            throw new RegraNegocioException("Preço inválido: " + preco);
        }
    }

    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));
        return produtoRepository.findByRestaurante(restaurante);
    }
}
