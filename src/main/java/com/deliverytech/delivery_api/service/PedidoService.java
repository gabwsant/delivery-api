package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.entity.ItemPedido;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         RestauranteRepository restauranteRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
    }

    // =================== CRIAÇÃO E CÁLCULO ===================

    /**
     * POST /api/pedidos - Cria um novo pedido, persistindo no banco.
     */
    @Transactional
    public Pedido criarPedido(Long clienteId, Long restauranteId, Map<Long, Integer> itensPedido) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));

        if (!cliente.isAtivo()) throw new RegraNegocioException("Cliente inativo");
        if (!restaurante.isAtivo()) throw new RegraNegocioException("Restaurante inativo");
        if (itensPedido == null || itensPedido.isEmpty()) {
            throw new RegraNegocioException("O pedido deve ter pelo menos um item");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatus("PENDENTE");
        pedido.setDataPedido(LocalDateTime.now());

        List<ItemPedido> listaDeItens = new ArrayList<>();

        // Processamento e validação de itens (lógica correta)
        for (Map.Entry<Long, Integer> item : itensPedido.entrySet()) {
            Long produtoId = item.getKey();
            Integer quantidade = item.getValue();

            if (quantidade <= 0) {
                throw new RegraNegocioException("A quantidade do produto " + produtoId + " deve ser positiva");
            }

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto " + produtoId + " não encontrado"));

            if (!produto.isAtivo()) {
                throw new RegraNegocioException("Produto " + produto.getNome() + " está indisponível");
            }
            if (!produto.getRestaurante().getId().equals(restauranteId)) {
                throw new RegraNegocioException("Produto " + produto.getNome() + " não pertence ao restaurante " + restaurante.getNome());
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(quantidade);
            itemPedido.setPrecoUnitario(produto.getPreco());

            listaDeItens.add(itemPedido);
        }

        pedido.setItens(listaDeItens);
        pedido.calcularTotal();

        return pedidoRepository.save(pedido);
    }

    /**
     * POST /api/pedidos/calcular - Calcula o total do pedido sem salvar.
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotal(Long restauranteId, Map<Long, Integer> itensPedido) {
        BigDecimal totalProdutos = BigDecimal.ZERO;

        // Processamento e validação de itens (similar ao criarPedido)
        for (Map.Entry<Long, Integer> item : itensPedido.entrySet()) {
            Long produtoId = item.getKey();
            Integer quantidade = item.getValue();

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto " + produtoId + " não encontrado"));

            if (!produto.isAtivo()) {
                throw new RegraNegocioException("Produto " + produto.getNome() + " está indisponível");
            }

            // Opcional: Adicionar validação de que o produto pertence ao restaurante, se relevante para o cálculo.

            BigDecimal subtotal = produto.getPreco().multiply(new BigDecimal(quantidade));
            totalProdutos = totalProdutos.add(subtotal);
        }

        // Adicionar lógica de taxa de entrega se for um requisito de negócio
        return totalProdutos;
    }

    // =================== BUSCAS (READ) ===================

    /**
     * GET /api/pedidos/{id} - Busca um pedido completo por ID.
     * **CORRIGIDO** para usar o método padronizado do Repositório: findByIdCompleto
     */
    @Transactional(readOnly = true)
    public Pedido buscarPorIdCompleto(Long id) {
        // Substitui pedidoRepository.findByIdWithItens(id)
        Pedido pedido = pedidoRepository.findByIdCompleto(id);
        if (pedido == null) throw new EntidadeNaoEncontradaException("Pedido não encontrado");
        return pedido;
    }

    /**
     * GET /api/clientes/{clienteId}/pedidos - Histórico do cliente.
     * **CORRIGIDO** para usar o método padronizado do Repositório: findByClienteIdCompleto
     */
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntidadeNaoEncontradaException("Cliente não encontrado");
        }
        // Substitui pedidoRepository.findByClienteWithItens(clienteId)
        return pedidoRepository.findByClienteIdCompleto(clienteId);
    }

    /**
     * GET /api/restaurantes/{restauranteId}/pedidos - Pedidos do restaurante.
     * **CORRIGIDO** para usar o método padronizado do Repositório: findByRestauranteIdCompleto
     */
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorRestaurante(Long restauranteId) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new EntidadeNaoEncontradaException("Restaurante não encontrado");
        }
        // Substitui pedidoRepository.findByRestauranteIdWithItens(restauranteId)
        return pedidoRepository.findByRestauranteIdCompleto(restauranteId);
    }

    /**
     * GET /api/pedidos - Listar com filtros dinâmicos.
     * **CORRIGIDO** para usar o método padronizado do Repositório: listarComFiltrosCompleto
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarComFiltros(String status, LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new RegraNegocioException("A data de início não pode ser posterior à data de fim.");
        }
        // Substitui pedidoRepository.listarComFiltros(...)
        return pedidoRepository.listarComFiltrosCompleto(status, dataInicio, dataFim);
    }

    // =================== ATUALIZAÇÃO E CANCELAMENTO ===================

    /**
     * PATCH /api/pedidos/{id}/status - Atualiza o status do pedido.
     * **CONSOLIDADO** e **CORRIGIDO**
     */
    @Transactional
    public Pedido atualizarStatus(Long pedidoId, String novoStatus) {
        // Usa o método buscarPorIdCompleto para garantir o carregamento do pedido.
        Pedido pedido = buscarPorIdCompleto(pedidoId);

        // Adicionar validação de transição de status aqui (ex: PENDENTE -> APROVADO)

        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    /**
     * DELETE /api/pedidos/{id} - Cancela o pedido.
     * **CONSOLIDADO**
     */
    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = buscarPorIdCompleto(id);

        if (!pedido.getStatus().equals("PENDENTE")) {
            throw new RegraNegocioException("Pedido em status '" + pedido.getStatus() + "' não pode ser cancelado.");
        }

        pedido.setStatus("CANCELADO");
        pedidoRepository.save(pedido);
    }
}