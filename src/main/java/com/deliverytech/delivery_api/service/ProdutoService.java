package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // =================== CREATE ===================
    public Produto cadastrar(Long restauranteId, Produto produto) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));

        if(!restaurante.isAtivo()) {
            throw new RegraNegocioException("Não é possível adicionar produto a restaurante inativo");
        }

        validarPreco(produto.getPreco());
        produto.setRestaurante(restaurante);
        produto.setAtivo(true);

        return produtoRepository.save(produto);
    }

    // =================== READ ===================
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado"));
    }

    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        // Não é necessário buscar o restaurante primeiro, o Repositório pode fazer a busca direto pelo ID
        return produtoRepository.findByRestauranteId(restauranteId);
        // Se a regra for buscar apenas produtos *ativos* do restaurante, use:
        // Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));
        // return produtoRepository.findByRestauranteAndAtivoTrue(restaurante);
    }

    /**
     * NOVO MÉTODO: Suporta GET /api/produtos/categoria/{categoria}
     */
    public List<Produto> buscarPorCategoria(String categoria) {
        // O repositório já possui o método findByRestauranteCategoria (que busca nos restaurantes)
        return produtoRepository.findByRestauranteCategoria(categoria);
    }

    /**
     * NOVO MÉTODO: Suporta GET /api/produtos/buscar?nome={nome}
     */
    public List<Produto> buscarPorNome(String nome) {
        // O repositório deve ter o método findByNomeContainingIgnoreCase
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // =================== UPDATE ===================
    public Produto atualizar(Long id, Produto novosDados) {
        Produto existente = produtoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado"));

        validarPreco(novosDados.getPreco());

        existente.setNome(novosDados.getNome());
        existente.setDescricao(novosDados.getDescricao());
        existente.setPreco(novosDados.getPreco());
        existente.setAtivo(novosDados.isAtivo());

        return produtoRepository.save(existente);
    }

    // PATCH /api/produtos/{id}/disponibilidade
    @Transactional
    public void alterarDisponibilidade(Long id, boolean ativo) {
        if (!produtoRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Produto não encontrado");
        }
        produtoRepository.setAtivo(id, ativo);
    }

    // =================== DELETE ===================
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Produto não encontrado");
        }
        produtoRepository.deleteById(id);
    }

    // =================== VALIDAÇÃO ===================
    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O preço do produto não pode ser nulo ou menor/igual a zero.");
        }
    }
}